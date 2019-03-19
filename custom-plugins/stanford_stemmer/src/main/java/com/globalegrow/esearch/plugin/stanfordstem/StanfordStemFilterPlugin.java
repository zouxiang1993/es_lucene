package com.globalegrow.esearch.plugin.stanfordstem;

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
 *  File: StanfordStemFilterPlugin.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/19				    zouxiang				Initial.
 *
 * </pre>
 */
public class StanfordStemFilterPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map map = new HashMap();
        map.put("stanford_stem", new AnalysisModule.AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
                return new StanfordStemFilterFactory(indexSettings, name, settings);
            }

            @Override
            public boolean requiresAnalysisSettings() {
                return true;
            }
        });
        return map;
    }
}
