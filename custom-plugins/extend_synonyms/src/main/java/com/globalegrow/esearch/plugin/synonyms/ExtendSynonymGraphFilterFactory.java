package com.globalegrow.esearch.plugin.synonyms;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.synonym.SolrSynonymParser;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.elasticsearch.common.io.FastStringReader;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *
 *  File: ExtendSynonymGraphFilterFactory.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/20				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendSynonymGraphFilterFactory extends AbstractTokenFilterFactory {
    protected final SynonymMap synonymMap;
    protected final boolean ignoreCase;
    private static Logger logger = ESLoggerFactory.getLogger("extend-synonyms-graph");

    public ExtendSynonymGraphFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings, AnalysisRegistry analysisRegistry) throws IOException {
        super(indexSettings, name, settings);

        String synonymsUrl = settings.get("synonyms_url");
        if (synonymsUrl == null || synonymsUrl.trim().length() == 0) {
            String message = String.format(Locale.ROOT, "synonyms_url is required.");
            throw new IllegalArgumentException(message);
        }

        List<String> rules = getSynonymsFromUrl(synonymsUrl);
        StringBuilder sb = new StringBuilder();
        for (String line : rules) {
            sb.append(line).append(System.lineSeparator());
        }
        Reader rulesReader = new FastStringReader(sb.toString());

        this.ignoreCase = settings.getAsBoolean("ignore_case", false);
        boolean expand = settings.getAsBoolean("expand", true);

        String stemmerName = settings.get("stemmer", null); // 默认不使用stemmer
        TokenFilterFactory tokenFilterFactoryTemp = null;
        if (stemmerName != null) {
            AnalysisModule.AnalysisProvider<TokenFilterFactory> tokenFilterFactoryFactory =
                    analysisRegistry.getTokenFilterProvider(stemmerName, indexSettings);
            if (tokenFilterFactoryFactory == null) {
                throw new IllegalArgumentException("failed to find stemmer token filter  [" + stemmerName + "] for synonym token filter");
            }
            tokenFilterFactoryTemp = tokenFilterFactoryFactory.get(indexSettings, environment, stemmerName,
                    AnalysisRegistry.getSettingsFromIndexSettings(indexSettings, AnalysisRegistry.INDEX_ANALYSIS_TOKENIZER + "." + stemmerName));
            if (tokenFilterFactoryTemp == null) {
                throw new IllegalArgumentException("failed to find stemmer token filter  [" + stemmerName + "] for synonym token filter");
            }
        }
        final TokenFilterFactory tokenFilterFactory = tokenFilterFactoryTemp;

        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new WhitespaceTokenizer();
                TokenStream stream = ignoreCase ? new LowerCaseFilter(tokenizer) : tokenizer;
                if (tokenFilterFactory != null) {
                    stream = tokenFilterFactory.create(stream); // 应用stemmer
                }
                return new TokenStreamComponents(tokenizer, stream);
            }
        };

        try {
            // 这里只支持SolrSynonymMap，不支持wordnet类型的。
            SolrSynonymParser parser = new SolrSynonymParser(true, expand, analyzer);
            parser.parse(rulesReader);
            synonymMap = parser.build();
        } catch (Exception e) {
            throw new IllegalArgumentException("failed to build synonyms", e);
        }
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return synonymMap.fst == null ? tokenStream : new SynonymGraphFilter(tokenStream, synonymMap, ignoreCase);
    }

    private static final List<String> DEFAULT_RULES = Arrays.asList("token1 => token2");

    static List<String> getSynonymsFromUrl(String url) {
        try {
            logger.info("get synonyms, url : " + url);
            String rules = HttpUtil.sendPost(url, "{\"lastUpdateTime\": \"\"}");
            if (rules == null || rules.isEmpty()) {
                return DEFAULT_RULES;
            }
            Object dataObj = JSON.parseObject(rules).get("synonym");
            if (dataObj == null) {
                return DEFAULT_RULES;
            }
            String data = dataObj.toString();
            String[] lines = data.split("\n");
            return Arrays.asList(lines);
        } catch (Exception e) {
            logger.error("get synonym error", e);
            return DEFAULT_RULES;
        }
    }
}
