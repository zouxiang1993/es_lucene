// MaxentTagger -- StanfordMaxEnt, A Maximum Entropy Toolkit
// Copyright (c) 2002-2016 Leland Stanford Junior University

// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

// For more information, bug reports, fixes, contact:
// Christopher Manning
// Dept of Computer Science, Gates 2A
// Stanford CA 94305-9020
// USA
// Support/Questions: stanford-nlp on SO or java-nlp-user@lists.stanford.edu
// Licensing: java-nlp-support@lists.stanford.edu
// http://nlp.stanford.edu/software/tagger.html

package edu.stanford.nlp.tagger.maxent;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.maxent.iis.LambdaSolve;
import edu.stanford.nlp.tagger.common.Tagger;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.ReflectionLoading;
import edu.stanford.nlp.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class MaxentTagger extends Tagger implements Serializable {

    private static final MaxentTagger instance = new MaxentTagger("model/english-left3words-distsim.tagger");

    public static MaxentTagger getInstance() {
        return instance;
    }

    private static Properties genProp(String modelFile) {
        Properties props = new Properties();
        props.setProperty("model", modelFile);
        return props;
    }

    public MaxentTagger(String modelFile) {
        this(modelFile, genProp(modelFile), true);
    }

    public MaxentTagger(String modelFile, Properties config, boolean printLoading) {
        readModelAndInit(config, modelFile, printLoading);
    }

    final Dictionary dict = new Dictionary();
    TTags tags;

    /**
     * Will return the index of a tag, adding it if it doesn't already exist
     */
    public int addTag(String tag) {
        return tags.add(tag);
    }

    /**
     * Will return the index of a tag if known, -1 if not already known
     */
    public int getTagIndex(String tag) {
        return tags.getIndex(tag);
    }

    public int numTags() {
        return tags.getSize();
    }

    public String getTag(int index) {
        return tags.getTag(index);
    }

    public Set<String> tagSet() {
        return tags.tagSet();
    }

    private LambdaSolveTagger prob;
    // For each extractor index, we have a map from possible extracted
    // features to an array which maps from tag number to feature weight index in the lambdas array.
    List<Map<String, int[]>> fAssociations = Generics.newArrayList();
    //PairsHolder pairs = new PairsHolder();
    Extractors extractors;
    Extractors extractorsRare;
    AmbiguityClasses ambClasses;
    final boolean alltags = false;
    final Map<String, Set<String>> tagTokens = Generics.newHashMap();

    private static final int RARE_WORD_THRESH = Integer.parseInt(TaggerConfig.RARE_WORD_THRESH);
    private static final int MIN_FEATURE_THRESH = Integer.parseInt(TaggerConfig.MIN_FEATURE_THRESH);
    private static final int CUR_WORD_MIN_FEATURE_THRESH = Integer.parseInt(TaggerConfig.CUR_WORD_MIN_FEATURE_THRESH);
    private static final int RARE_WORD_MIN_FEATURE_THRESH = Integer.parseInt(TaggerConfig.RARE_WORD_MIN_FEATURE_THRESH);
    private static final int VERY_COMMON_WORD_THRESH = Integer.parseInt(TaggerConfig.VERY_COMMON_WORD_THRESH);

    private static final boolean OCCURRING_TAGS_ONLY = Boolean.parseBoolean(TaggerConfig.OCCURRING_TAGS_ONLY);
    private static final boolean POSSIBLE_TAGS_ONLY = Boolean.parseBoolean(TaggerConfig.POSSIBLE_TAGS_ONLY);

    private double defaultScore;
    private double[] defaultScores; // = null;

    int leftContext;
    int rightContext;

    TaggerConfig config;

    /**
     * Determines which words are considered rare.  All words with count
     * in the training data strictly less than this number (standardly, &lt; 5) are
     * considered rare.
     */
    private int rareWordThresh = RARE_WORD_THRESH;

    /**
     * Determines which features are included in the model.  The model
     * includes features that occurred strictly more times than this number
     * (standardly, &gt; 5) in the training data.  Here I look only at the
     * history (not the tag), so the history appearing this often is enough.
     */
    int minFeatureThresh = MIN_FEATURE_THRESH;

    /**
     * This is a special threshold for the current word feature.
     * Only words that have occurred strictly &gt; this number of times
     * in total will generate word features with all of their occurring tags.
     * The traditional default was 2.
     */
    int curWordMinFeatureThresh = CUR_WORD_MIN_FEATURE_THRESH;

    /**
     * Determines which rare word features are included in the model.
     * The features for rare words have a strictly higher support than
     * this number are included. Traditional default is 10.
     */
    int rareWordMinFeatureThresh = RARE_WORD_MIN_FEATURE_THRESH;

    /**
     * If using tag equivalence classes on following words, words that occur
     * strictly more than this number of times (in total with any tag)
     * are sufficiently frequent to form an equivalence class
     * by themselves. (Not used unless using equivalence classes.)
     * <p>
     * There are places in the code (ExtractorAmbiguityClass.java, for one)
     * that assume this value is constant over the life of a tagger.
     */
    int veryCommonWordThresh = VERY_COMMON_WORD_THRESH;


    int xSize;
    int ySize;
    boolean occurringTagsOnly = OCCURRING_TAGS_ONLY;
    boolean possibleTagsOnly = POSSIBLE_TAGS_ONLY;

    private boolean initted = false;

    boolean VERBOSE = false;

    Function<String, String> wordFunction;


    /* Package access - shouldn't be part of public API. */
    LambdaSolve getLambdaSolve() {
        return prob;
    }

    // TODO: make these constructors instead of init methods?
    void init(TaggerConfig config) {
        if (initted) return;  // TODO: why not reinit?

        this.config = config;

        String lang, arch;
        String[] openClassTags, closedClassTags;

        if (config == null) {
            lang = "english";
            arch = "left3words";
            openClassTags = StringUtils.EMPTY_STRING_ARRAY;
            closedClassTags = StringUtils.EMPTY_STRING_ARRAY;
            wordFunction = null;
        } else {
            this.VERBOSE = config.getVerbose();

            lang = config.getLang();
            arch = config.getArch();
            openClassTags = config.getOpenClassTags();
            closedClassTags = config.getClosedClassTags();
            if (!config.getWordFunction().equals("")) {
                wordFunction =
                        ReflectionLoading.loadByReflection(config.getWordFunction());
            }

            if (((openClassTags.length > 0) && !lang.equals("")) || ((closedClassTags.length > 0) && !lang.equals("")) || ((closedClassTags.length > 0) && (openClassTags.length > 0))) {
                throw new RuntimeException("At least two of lang (\"" + lang + "\"), openClassTags (length " + openClassTags.length + ": " + Arrays.toString(openClassTags) + ")," +
                        "and closedClassTags (length " + closedClassTags.length + ": " + Arrays.toString(closedClassTags) + ") specified---you must choose one!");
            } else if ((openClassTags.length == 0) && lang.equals("") && (closedClassTags.length == 0) && !config.getLearnClosedClassTags()) {
//        log.info("warning: no language set, no open-class tags specified, and no closed-class tags specified; assuming ALL tags are open class tags");
            }
        }

        if (openClassTags.length > 0) {
            tags = new TTags();
            tags.setOpenClassTags(openClassTags);
        } else if (closedClassTags.length > 0) {
            tags = new TTags();
            tags.setClosedClassTags(closedClassTags);
        } else {
            tags = new TTags(lang);
        }

        defaultScore = lang.equals("english") ? 1.0 : 0.0;

        if (config != null) {
            rareWordThresh = config.getRareWordThresh();
            minFeatureThresh = config.getMinFeatureThresh();
            curWordMinFeatureThresh = config.getCurWordMinFeatureThresh();
            rareWordMinFeatureThresh = config.getRareWordMinFeatureThresh();
            veryCommonWordThresh = config.getVeryCommonWordThresh();
            occurringTagsOnly = config.occurringTagsOnly();
            possibleTagsOnly = config.possibleTagsOnly();
            // log.info("occurringTagsOnly: "+occurringTagsOnly);
            // log.info("possibleTagsOnly: "+possibleTagsOnly);

            if (config.getDefaultScore() >= 0)
                defaultScore = config.getDefaultScore();
        }

        // just in case, reset the defaultScores array so it will be
        // recached later when needed.  can't initialize it now in case we
        // don't know ysize yet
        defaultScores = null;

        if (config == null || config.getMode() == TaggerConfig.Mode.TRAIN) {
            // initialize the extractors based on the arch variable
            // you only need to do this when training; otherwise they will be
            // restored from the serialized file
            extractors = new Extractors(ExtractorFrames.getExtractorFrames(arch));
            extractorsRare = new Extractors(ExtractorFramesRare.getExtractorFramesRare(arch, tags));

            setExtractorsGlobal();
        }

        ambClasses = new AmbiguityClasses(tags);

        initted = true;
    }


    private synchronized void initDefaultScores() {
        if (defaultScores == null) {
            defaultScores = new double[ySize + 1];
            for (int i = 0; i < ySize + 1; ++i) {
                defaultScores[i] = Math.log(i * defaultScore);
            }
        }
    }

    /**
     * Caches a math log operation to save a tiny bit of time
     */
    double getInactiveTagDefaultScore(int nDefault) {
        if (defaultScores == null) {
            initDefaultScores();
        }
        return defaultScores[nDefault];
    }

    boolean hasApproximateScoring() {
        return defaultScore > 0.0;
    }

    /**
     * Read the extractors from a stream.
     */
    private void readExtractors(InputStream file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(file);
        extractors = (Extractors) in.readObject();
        extractorsRare = (Extractors) in.readObject();
        extractors.initTypes();
        extractorsRare.initTypes();
        int left = extractors.leftContext();
        int left_u = extractorsRare.leftContext();
        if (left_u > left) {
            left = left_u;
        }
        leftContext = left;
        int right = extractors.rightContext();
        int right_u = extractorsRare.rightContext();
        if (right_u > right) {
            right = right_u;
        }
        rightContext = right;

        setExtractorsGlobal();
    }

    // Sometimes there is data associated with the tagger (such as a
    // dictionary) that we don't want saved with each extractor.  This
    // call lets those extractors get that information from the tagger
    // after being loaded from a data file.
    private void setExtractorsGlobal() {
        extractors.setGlobalHolder(this);
        extractorsRare.setGlobalHolder(this);
    }

    protected void readModelAndInit(Properties config, String modelFileOrUrl, boolean printLoading) {
        try (InputStream is = IOUtils.getInputStreamFromURLOrClasspathOrFileSystem(modelFileOrUrl)) {
            readModelAndInit(config, is, printLoading);
        } catch (IOException e) {
            throw new RuntimeIOException("Error while loading a tagger model (probably missing model file)", e);
        }
    }

    protected void readModelAndInit(Properties config, InputStream modelStream, boolean printLoading) {
        try {
            // first check can open file ... or else leave with exception
            DataInputStream rf = new DataInputStream(modelStream);

            readModelAndInit(config, rf, printLoading);
            rf.close();
        } catch (IOException e) {
            throw new RuntimeIOException("Error while loading a tagger model (probably missing model file)", e);
        }
    }

    protected void readModelAndInit(Properties config, DataInputStream rf, boolean printLoading) {
        try {
            TaggerConfig taggerConfig = TaggerConfig.readConfig(rf);
            if (config != null) {
                taggerConfig.setProperties(config);
            }
            // then init tagger
            init(taggerConfig);

            xSize = rf.readInt();
            ySize = rf.readInt();
            // dict = new Dictionary();  // this method is called in constructor, and it's initialized as empty already
            dict.read(rf);

            tags.read(rf);
            readExtractors(rf);
            dict.setAmbClasses(ambClasses, veryCommonWordThresh, tags);

            int[] numFA = new int[extractors.size() + extractorsRare.size()];
            int sizeAssoc = rf.readInt();
            fAssociations = Generics.newArrayList();
            for (int i = 0; i < extractors.size() + extractorsRare.size(); ++i) {
                fAssociations.add(Generics.<String, int[]>newHashMap());
            }

            for (int i = 0; i < sizeAssoc; i++) {
                int numF = rf.readInt();
                FeatureKey fK = new FeatureKey();
                fK.read(rf);
                numFA[fK.num]++;

                // TODO: rewrite the writing / reading code to store
                // fAssociations in a cleaner manner?  Only do this when
                // rebuilding all the tagger models anyway.  When we do that, we
                // can get rid of FeatureKey
                Map<String, int[]> fValueAssociations = fAssociations.get(fK.num);
                int[] fTagAssociations = fValueAssociations.get(fK.val);
                if (fTagAssociations == null) {
                    fTagAssociations = new int[ySize];
                    for (int j = 0; j < ySize; ++j) {
                        fTagAssociations[j] = -1;
                    }
                    fValueAssociations.put(fK.val, fTagAssociations);
                }
                fTagAssociations[tags.getIndex(fK.tag)] = numF;
            }

            prob = new LambdaSolveTagger(rf);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeIOException("Error while loading a tagger model (probably missing model file)", e);
        }
    }

    /* Package access so it doesn't appear in public API. */
    boolean isRare(String word) {
        return dict.sum(word) < rareWordThresh;
    }

    public List<TaggedWord> tagSentence(List<? extends HasWord> sentence,
                                        boolean reuseTags) {
        TestSentence testSentence = new TestSentence(this);
        return testSentence.tagSentence(sentence, reuseTags);   // reuseTags应设置为false
    }
}