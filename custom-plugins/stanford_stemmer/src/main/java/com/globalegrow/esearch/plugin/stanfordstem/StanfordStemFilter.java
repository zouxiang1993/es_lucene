package com.globalegrow.esearch.plugin.stanfordstem;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

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

    public StanfordStemFilter(TokenStream in) {
        super(in);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        if (!keywordAttr.isKeyword()) { // 支持stemmer_override
            stem();
        }
        return true;
    }

//    private static final StanfordCoreNLP pipeline;
//    static {
//        Properties props = new Properties();
//        // 要用到的是lemma, 它依赖 tokenize, ssplit, pos
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
//
//        pipeline = new StanfordCoreNLP(props);
//    }

    /**
     * 使用Stanford CoreNLP 对当前token进行词干提取
     */
    private void stem() {
        try {
            final String originToken = termAtt.toString();
            Sentence sentence = new Sentence(Arrays.asList(originToken));
            Token token = new Token(sentence, 0);
            String lemma = token.lemma();

//            CoreDocument document = new CoreDocument(originToken);
//            pipeline.annotate(document);
//            String lemma = document.tokens().get(0).lemma();

            if (originToken.equals(lemma)) { // 没有发生变化
                return;
            } else {
                termAtt.setEmpty().append(lemma);
            }

        } catch (Exception e) {
            logger.error("stem error", e);
        }
    }
}
