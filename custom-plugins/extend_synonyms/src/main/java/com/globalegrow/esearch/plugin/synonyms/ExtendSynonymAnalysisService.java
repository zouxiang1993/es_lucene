package com.globalegrow.esearch.plugin.synonyms;

import org.elasticsearch.common.component.AbstractLifecycleComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalysisRegistry;

import java.io.IOException;

/**
 * <pre>
 *
 *  File: ExtendSynonymAnalysisService.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/21				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendSynonymAnalysisService extends AbstractLifecycleComponent {

    @Inject
    public ExtendSynonymAnalysisService(
            final Settings settings, final AnalysisRegistry analysisRegistry,
            final ExtendSynonymsFilterPlugin.PluginComponent pluginComponent) {
        super(settings);
        // 在这里注入AnalysisRegistry，后面查询stemmer时要用到
        pluginComponent.setAnalysisRegistry(analysisRegistry);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    protected void doClose() throws IOException {

    }
}
