package com.globalegrow.esearch.plugin.esof;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 *  File: ExtendStemmerOverrideFilterPlugin.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  扩展ES的StemmerOverrideTokenFilter, 支持从一个可配置的URL来获取词典配置。
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/28				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendStemmerOverrideFilterPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map map = new HashMap();
        map.put("extend_stemmer_override", new AnalysisModule.AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
                return new ExtendStemmerOverrideFilterFactory(indexSettings, environment, name, settings);
            }

            @Override
            public boolean requiresAnalysisSettings() {
                return true;
            }
        });
        return map;
    }

}
