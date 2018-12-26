package com.globalegrow.esearch.plugin.extend_mapping;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 *
 *  File: ExtendMappingCharFilterPlugin.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  扩展MappingCharFilter, 支持了不存在的mapping_path 配置
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/26				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendMappingCharFilterPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<CharFilterFactory>> getCharFilters() {
        Map map = new HashMap();
        map.put("extend_mapping", new AnalysisModule.AnalysisProvider<CharFilterFactory>() {
            @Override
            public CharFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings)
                    throws IOException {
                return new ExtendMappingCharFilterFactory(indexSettings, environment, name, settings);
            }

            @Override
            public boolean requiresAnalysisSettings() {
                return true;
            }
        });
        return map;
    }
}
