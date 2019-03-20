package com.globalegrow.esearch.plugin.stanfordstem;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * <pre>
 *
 *  File: StanfordStemFilterTest.java
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
public class StanfordStemFilterTest {
    Analyzer stanfordAnalyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer tok = new StandardTokenizer();
            TokenStream sink = new StandardFilter(tok);
            sink = new LowerCaseFilter(sink);
            sink = new StanfordStemFilter(sink);
            return new TokenStreamComponents(tok, sink);
        }
    };
    Analyzer porterAnalyzer = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            Tokenizer tok = new StandardTokenizer();
            TokenStream sink = new StandardFilter(tok);
            sink = new LowerCaseFilter(sink);
            sink = new PorterStemFilter(sink);
            return new TokenStreamComponents(tok, sink);
        }
    };

    /**
     * Stanford stem 和 Porter stem 词干提取 效果对比
     */
    @Test
    public void testStanfordStemAndPorterStem() {
//        final String text = "ear earring earrings cloth clothes clothing micro";
        final String text = "front knot knotted bikini earrings";
        String stanfordResult = analyze(stanfordAnalyzer, text);
        String porterResult = analyze(porterAnalyzer, text);
        System.out.println("origin  \t" + text);
        System.out.println("stanford\t" + stanfordResult);
        System.out.println("porter  \t" + porterResult);
    }

    @Test
    public void test2() throws Exception {
        URI uri = StanfordStemFilterTest.class.getClassLoader().getResource("zaful.txt").toURI();
        File file = Paths.get(uri).toFile();
        FileReader fileReader = new FileReader(file);
        Scanner scanner = new Scanner(fileReader);

        FileWriter fw = new FileWriter("E:\\GB\\result.txt");

        int lineTotal = 0; // 总行数
        int diffTotal = 0;  // stanford和porter_stem结果有差异的总数
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lineTotal++;
            String stanfordResult = analyze(stanfordAnalyzer, line);
            String porterResult = analyze(porterAnalyzer, line);
            // 有差异才写入到结果集中
            if (!porterResult.equals(stanfordResult)) {
                diffTotal++;
                fw.write("原文本:\t" + line);
                fw.write("\r\n");
                fw.write("stanford:\t：" + stanfordResult);
                fw.write("\r\n");
                fw.write("porter_stem:\t" + porterResult);
                fw.write("\r\n");
                fw.write("\r\n");
            }
        }
        fw.close();
        scanner.close();
        System.out.println("lineTotal=" + lineTotal);
        System.out.println("difTotal=" + diffTotal);
    }

    private String analyze(Analyzer analyzer, String text) {
        try {
            TokenStream ts = analyzer.tokenStream("", text);
            CharTermAttribute termAttr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            StringBuilder sb = new StringBuilder();
            while (ts.incrementToken()) {
                sb.append(termAttr.toString() + " ");
            }
            ts.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR!";
        }
    }
}
