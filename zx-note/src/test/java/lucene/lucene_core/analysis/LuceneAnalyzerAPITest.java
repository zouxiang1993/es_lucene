package lucene.lucene_core.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;

/**
 * <pre>
 *
 *  File: LuceneAnalyzerAPITest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/8				    zouxiang				Initial.
 *
 * </pre>
 */
public class LuceneAnalyzerAPITest {
    @Test
    public void testAnalyzer() throws IOException {
        Analyzer analyzer = new Analyzer() {

            @Override
            protected Reader initReader(String fieldName, Reader reader) {
                return new HTMLStripCharFilter(reader); // 去除HTML标签
            }

            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new WhitespaceTokenizer();  // 空格分词
                TokenStream ts = new LowerCaseFilter(tokenizer);  // 转小写
                ts = new PorterStemFilter(ts);  // 词干提取
                return new TokenStreamComponents(tokenizer, ts);
            }
        };
        final String text = "<b>CAT running</b>";
        TokenStream ts = analyzer.tokenStream("", text);
        CharTermAttribute charTermAtt = ts.getAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String term = charTermAtt.toString();
            System.out.println(term); // 结果： cat   \n  run
        }
    }
}
