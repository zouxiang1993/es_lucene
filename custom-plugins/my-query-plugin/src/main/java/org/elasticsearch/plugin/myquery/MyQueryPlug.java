package org.elasticsearch.plugin.myquery;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.common.settings.ClusterSettings;
import org.elasticsearch.common.settings.IndexScopedSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestHandler;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * <pre>
 *
 *  File: MyQueryPlug.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/9/18				    zouxiang				Initial.
 *
 * </pre>
 */
public class MyQueryPlug extends Plugin implements ActionPlugin {

    static {
        System.out.println("加载了 class MyQueryPlug");
    }

    public MyQueryPlug() {
        System.out.println("创建了 MyQueryPlug.....");
    }

    @Override
    public List<RestHandler> getRestHandlers(Settings settings, RestController restController, ClusterSettings clusterSettings, IndexScopedSettings indexScopedSettings, SettingsFilter settingsFilter, IndexNameExpressionResolver indexNameExpressionResolver, Supplier<DiscoveryNodes> nodesInCluster) {
        return Collections.singletonList(new RestMyQueryAction(settings, restController));
    }
}
