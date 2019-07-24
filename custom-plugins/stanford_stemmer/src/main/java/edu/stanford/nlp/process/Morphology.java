package edu.stanford.nlp.process;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;


/**
 * Morphology computes the base form of English words, by removing just
 * inflections (not derivational morphology).  That is, it only does noun
 * plurals, pronoun case, and verb endings, and not things like comparative adjectives
 * or derived nominals.  It is based on a finite-state
 * transducer implemented by John Carroll et al., written in flex and publicly
 * available.
 * See: http://www.informatics.susx.ac.uk/research/nlp/carroll/morph.html .
 * There are several ways of invoking Morphology. One is by calling the static
 * methods:
 * <ul>
 * <li> WordTag stemStatic(String word, String tag) </li>
 * <li> WordTag stemStatic(WordTag wordTag) </li>
 * </ul>
 * If we have created a Morphology object already we can use the methods
 * WordTag stem(String word, string tag) or WordTag stem(WordTag wordTag).
 * <p>
 * Another way of using Morphology is to run it on an input file by running
 * {@code java Morphology filename}.  In this case, POS tags MUST be
 * separated from words by an underscore ("_").
 * <p>
 * Note that a single instance of Morphology is not thread-safe, as
 * the underlying lexer object is not built to be re-entrant.  One thing that
 * you can do to get around this is build a new Morphology object for
 * each thread or each set of calls to the Morphology.  For example, the
 * MorphaAnnotator builds a Morphology for each document it annotates.
 * The other approach is to use the synchronized methods in this class.
 * The crucial lexer-accessing portion of all the static methods is synchronized
 * (otherwise, their use tended to be threading bugs waiting to happen).
 * If you want less synchronization, create your own Morphology objects.
 *
 * @author Kristina Toutanova (kristina@cs.stanford.edu)
 * @author Christopher Manning
 */
public class Morphology {

    private final Morpha lexer;

    public Morphology() {
        lexer = new Morpha(new InputStreamReader(System.in));
    }

    /**
     * Process morphologically words from a Reader.
     *
     * @param in The Reader to read from
     */
    public Morphology(Reader in) {
        lexer = new Morpha(in);
    }

    public Morphology(Reader in, int flags) {
        lexer = new Morpha(in);
        lexer.setOptions(flags);
    }

    public String stem(String word) {
        try {
            lexer.yyreset(new StringReader(word));
            lexer.yybegin(Morpha.any);
            String wordRes = lexer.next();
            return wordRes;
        } catch (IOException e) {
            // TODO: log
//            log.warning("Morphology.stem() had error on word " + word);
            return word;
        }
    }


    public String lemma(String word, String tag) {
        return lemmatize(word, tag, lexer, lexer.option(1));
    }

    public String lemma(String word, String tag, boolean lowercase) {
        return lemmatize(word, tag, lexer, lowercase);
    }

    /**
     * Lemmatize the word, being sensitive to the tag, using the
     * passed in lexer.
     *
     * @param lowercase If this is true, words other than proper nouns will
     *                  be changed to all lowercase.
     */
    private static String lemmatize(String word, String tag, Morpha lexer, boolean lowercase) {
        boolean wordHasForbiddenChar = word.indexOf('_') >= 0 || word.indexOf(' ') >= 0 || word.indexOf('\n') >= 0;
        String quotedWord = word;
        if (wordHasForbiddenChar) {
            // choose something unlikely. Classical Vedic!
            quotedWord = quotedWord.replaceAll("_", "\u1CF0");
            quotedWord = quotedWord.replaceAll(" ", "\u1CF1");
            quotedWord = quotedWord.replaceAll("\n", "\u1CF2");
        }
        String wordtag = quotedWord + '_' + tag;
        try {
            lexer.setOption(1, lowercase);
            lexer.yyreset(new StringReader(wordtag));
            lexer.yybegin(Morpha.scan);
            String wordRes = lexer.next();
            lexer.next(); // go past tag
            if (wordHasForbiddenChar) {
                wordRes = wordRes.replaceAll("\u1CF0", "_");
                wordRes = wordRes.replaceAll("\u1CF1", " ");
                wordRes = wordRes.replaceAll("\u1CF2", "\n");
            }
            return wordRes;
        } catch (IOException e) {
            // TODO: log
//            log.warning("Morphology.stem() had error on word " + word + '/' + tag);
            return word;
        }
    }

}
