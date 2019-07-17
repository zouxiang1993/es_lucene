package com.globalegrow.esearch.plugin.stanfordstem;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
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
    private static final Properties EMPTY_PROPS;

    static {
        Properties prop = null;
        try {
            Field field = Document.class.getDeclaredField("EMPTY_PROPS");
            field.setAccessible(true);
            prop= (Properties) field.get(null);
        }catch (Exception e){
            logger.error("StanfordStemFilter初始化失败", e);
        }
        EMPTY_PROPS = prop;
    }

    /**
     * 为了在ES启动时加载插件的过程中初始化Stanford CoreNLP , 默认是懒加载
     */
    protected static void init() {
        logger.info("StanfordStemFilter static初始化");
        AccessController.doPrivileged(
                new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        Sentence sentence = new Sentence("xxx");
                        Token token = new Token(sentence, 0);
                        String lemma = token.lemma();
                        return null;
                    }
                }
        );
    }

    public StanfordStemFilter(TokenStream in) {
        super(in);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken())
            return false;
        if (!keywordAttr.isKeyword()) { // 支持stemmer_override
            AccessController.doPrivileged( // Stanford CoreNLP需要某些权限
                    new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            stem();
                            return null;
                        }
                    }
            );
        }
        return true;
    }

    /**
     * 使用Stanford CoreNLP 对当前token进行词干提取
     */
    private void stem() {
        try {
            final String originToken = termAtt.toString();
            Document document = new Document(originToken);
            Sentence sentence = document.sentence(0, EMPTY_PROPS);

            Token token = new Token(sentence, 0);
            String lemma = token.lemma();

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
