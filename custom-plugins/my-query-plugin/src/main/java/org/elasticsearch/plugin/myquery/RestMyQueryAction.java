package org.elasticsearch.plugin.myquery;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;

import java.util.Collections;
import java.util.Set;

/**
 * <pre>
 *
 *  File: RestMyQueryAction.java
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
public class RestMyQueryAction extends BaseRestHandler {

    public static final String TYPED_KEYS_PARAM = "typed_keys";
    private static final Set<String> RESPONSE_PARAMS = Collections.singleton(TYPED_KEYS_PARAM);

    protected RestMyQueryAction(Settings settings, RestController restController) {
        super(settings);
        restController.registerHandler(RestRequest.Method.POST, "/my_query", this);
        restController.registerHandler(RestRequest.Method.GET, "/my_query", this);
    }

    @Override
    protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) {
        return null;
    }

    public static Set<String> getResponseParams() {
        return RESPONSE_PARAMS;
    }
}
