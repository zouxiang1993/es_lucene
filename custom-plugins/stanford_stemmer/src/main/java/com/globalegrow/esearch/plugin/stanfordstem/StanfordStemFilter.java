package com.globalegrow.esearch.plugin.stanfordstem;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *
 *  File: StanfordStemFilter.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/14				    zouxiang				Initial.
 *
 * </pre>
 */
public final class StanfordStemFilter extends TokenFilter {
    private static Logger logger = ESLoggerFactory.getLogger("stanford-stem");
    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);
    private static MaxentTagger maxentTagger;

    static {
        AccessController.doPrivileged(
                new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        maxentTagger = MaxentTagger.getInstance();
                        return null;
                    }
                }
        );
    }

    /**
     * 为了在ES启动时加载插件的过程中初始化Stanford CoreNLP , 默认是懒加载
     */
    protected static void init() {
        logger.info("StanfordStemFilter static初始化, (测试权限)");
        String result = stem("running");
        if (!"run".equalsIgnoreCase(result)) {
            logger.error("StanfordStemFilter初始化失败， 期望结果: run, 实际结果: " + result);
        }
    }

    public StanfordStemFilter(TokenStream in) {
        super(in);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        if (!keywordAttr.isKeyword()) { // 支持stemmer_override
            try {
                final String originToken = termAtt.toString();
                String result = stem(termAtt.toString());
                if (originToken.equals(result)) { // 没有发生变化
                } else {
                    termAtt.setEmpty().append(result);
                }
            } catch (Exception e) {
                logger.error("stem error", e);
            }
        }
        return true;
    }

    /**
     * 使用Stanford CoreNLP 对当前token进行词干提取
     */
    private static String stem(String word) {
        word = word.replaceAll("('|’)s$", ""); // 所有格的问题。
        String tag = tag(word); // 词性
        Morphology morphology = new Morphology();
        if (tag == null || tag.isEmpty()) {
            return morphology.lemma(word, null);
        } else {
            return morphology.lemma(word, tag);
        }
    }

    /**
     * 词性标注
     *
     * @param word
     * @return
     */
    private static String tag(final String word) {
        List<TaggedWord> taggedWords = maxentTagger.tagSentence(Arrays.asList(new HasWord() {
            @Override
            public String word() {
                return word;
            }

            @Override
            public void setWord(String word) {
                throw new UnsupportedOperationException();
            }
        }), false);
        if (taggedWords != null && taggedWords.size() > 0) {
            TaggedWord taggedWord = taggedWords.get(0);
            if (taggedWord != null) {
                return taggedWord.tag();
            }
        }
        return null;
    }
}
