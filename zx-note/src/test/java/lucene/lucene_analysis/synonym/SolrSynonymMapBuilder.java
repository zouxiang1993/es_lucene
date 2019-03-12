package lucene.lucene_analysis.synonym;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymMap;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *
 *  File: SolrSynonymMapBuilder.java
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
public class SolrSynonymMapBuilder {
    public static SynonymMap build(boolean expand, Analyzer analyzer, Reader in) throws Exception {
        SolrSynonymParser parser = new SolrSynonymParser(true, expand, analyzer);
        parser.parse(in);
        return parser.build();
    }

    /**
     * 将List<String>转换成可供SynonymMap直接消费的Reader。每个String表示配置文件中的一行
     *
     * @param lines
     * @return
     */
    public static Reader newReader(List<String> lines) {
        if (lines == null) {
            throw new IllegalArgumentException("lines must not be null");
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String line : lines) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(line);
        }
        return new StringReader(sb.toString());
    }

    public static void main(String[] args) {
        Arrays.asList(new String[]{
                "",
                "",
                ""
        });
    }
}
