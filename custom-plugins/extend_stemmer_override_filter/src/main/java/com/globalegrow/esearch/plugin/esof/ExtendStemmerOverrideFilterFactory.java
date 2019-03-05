package com.globalegrow.esearch.plugin.esof;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilter;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *
 *  File: ExtendStemmerOverrideFilterFactory.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/28				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendStemmerOverrideFilterFactory extends AbstractTokenFilterFactory {

    private final String RULES_URL = "rules_url";
    private static Logger logger = ESLoggerFactory.getLogger("extend-stemmer-override");
    private final StemmerOverrideFilter.StemmerOverrideMap overrideMap;

    public ExtendStemmerOverrideFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
        String rulesUrl = settings.get(RULES_URL);
        if (rulesUrl == null || rulesUrl.trim().length() == 0) {
            String message = String.format(Locale.ROOT, "%s param is required.", RULES_URL);
            throw new IllegalArgumentException(message);
        }

        List<String> rules = getRulesFromUrl(rulesUrl);
        StemmerOverrideFilter.Builder builder = new StemmerOverrideFilter.Builder(false);
        parseRules(rules, builder, "=>");
        overrideMap = builder.build();
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StemmerOverrideFilter(tokenStream, overrideMap);
    }

    private static final List<String> DEFAULT_RULES = Arrays.asList("token1 => token2");

    static List<String> getRulesFromUrl(String url) {
        try {
            logger.info("get stemmer override rules, url : " + url);
            String rules = HttpUtil.sendGet(url);
            if (rules == null || rules.isEmpty()) {
                return DEFAULT_RULES;
            }
            Object dataObj = JSON.parseObject(rules).get("data");
            if (dataObj == null) {
                return DEFAULT_RULES;
            }
            String data = dataObj.toString();
            String[] lines = data.split("\n");
            return Arrays.asList(lines);
        } catch (Exception e) {
            logger.error("get stemmer override rules error", e);
            return DEFAULT_RULES;
        }
    }

    static void parseRules(List<String> rules, StemmerOverrideFilter.Builder builder, String mappingSep) {
        for (String rule : rules) {
            String key, override;
            List<String> mapping = Strings.splitSmart(rule, mappingSep, false);
            if (mapping.size() == 2) {
                key = mapping.get(0).trim();
                override = mapping.get(1).trim();
            } else {
                throw new RuntimeException("Invalid Keyword override Rule:" + rule);
            }

            if (key.isEmpty() || override.isEmpty()) {
                throw new RuntimeException("Invalid Keyword override Rule:" + rule);
            } else {
                builder.add(key, override);
            }
        }
    }

}
