package lucene.lucene_analysis.synonym;

import lucene.lucene_core.analysis.TokenStreamToDot;
import lucene.lucene_core.analysis.TokenStreamToECharts;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *
 *  File: GraphvizVSEcharts.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/11				    zouxiang				Initial.
 *
 * </pre>
 */
public class GraphvizVSEcharts {

//    private final String text = "fast wi fi network is down";
    private final String text = "Mini Projctor";
    private TokenStream ts;

    @Before
    public void setup() throws Exception {
        List<String> lines = Arrays.asList(new String[]{
                "mini drone,mini", // 257行
//                "wi fi network, re dian",
//                "wi fi,wifi"
        });
        Reader reader = SolrSynonymMapBuilder.newReader(lines);
        SynonymMap synonymMap = SolrSynonymMapBuilder.build(false, new StandardAnalyzer(), reader);
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tok = new StandardTokenizer();
                TokenStream sink = new StandardFilter(tok);
                sink = new LowerCaseFilter(sink);
                sink = new PorterStemFilter(sink);
                sink = new SynonymFilter(sink, synonymMap, false);
                return new TokenStreamComponents(tok, sink);
            }
        };
        ts = analyzer.tokenStream("", text);
    }

    @Test
    public void testECharts() throws Exception {
        TokenStreamToECharts.getEchartsJson(ts);
    }

    @Test
    public void testGraphViz() throws Exception {
        final StringWriter sw = new StringWriter();
        new TokenStreamToDot(text, ts, new PrintWriter(sw)).toDot();
        System.out.println(sw.toString());
    }
}
