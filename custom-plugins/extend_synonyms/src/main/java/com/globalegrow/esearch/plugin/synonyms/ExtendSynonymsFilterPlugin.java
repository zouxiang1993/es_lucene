package com.globalegrow.esearch.plugin.synonyms;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.service.ClusterService;
import org.elasticsearch.common.component.LifecycleComponent;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AnalysisRegistry;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.watcher.ResourceWatcherService;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.singletonList;

/**
 * <pre>
 *
 *  File: ExtendSynonymsFilterPlugin.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  新增ExtendSynonymFilter和 ExtendSynonymGraphFilter,
 *  为SynonymFilter和SynonymGraphFilter提供
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/20				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendSynonymsFilterPlugin extends Plugin implements AnalysisPlugin {

    private PluginComponent pluginComponent = new PluginComponent();

    @Override
    public Collection<Object> createComponents(Client client, ClusterService clusterService, ThreadPool threadPool,
                                               ResourceWatcherService resourceWatcherService, ScriptService scriptService, NamedXContentRegistry xContentRegistry) {
        Collection<Object> components = new ArrayList<>();
        components.add(pluginComponent);
        return components;
    }

    @Override
    public Collection<Class<? extends LifecycleComponent>> getGuiceServiceClasses() {
        List<Class<ExtendSynonymAnalysisService>> lifecycleService = singletonList(ExtendSynonymAnalysisService.class);
        Object object = lifecycleService;
        return (Collection<Class<? extends LifecycleComponent>>) object;
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map map = new HashMap();
        map.put("extend_synonym", new AnalysisModule.AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
                return new ExtendSynonymFilterFactory(indexSettings, environment, name, settings, pluginComponent.getAnalysisRegistry());
            }

            @Override
            public boolean requiresAnalysisSettings() {
                return true;
            }
        });
        map.put("extend_synonym_graph", new AnalysisModule.AnalysisProvider<TokenFilterFactory>() {
            @Override
            public TokenFilterFactory get(IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
                return new ExtendSynonymGraphFilterFactory(indexSettings, environment, name, settings, pluginComponent.getAnalysisRegistry());
            }

            @Override
            public boolean requiresAnalysisSettings() {
                return true;
            }
        });
        return map;
    }

    public static class PluginComponent {

        private AnalysisRegistry analysisRegistry;

        public AnalysisRegistry getAnalysisRegistry() {
            return analysisRegistry;
        }

        public void setAnalysisRegistry(AnalysisRegistry analysisRegistry) {
            this.analysisRegistry = analysisRegistry;
        }
    }
}
