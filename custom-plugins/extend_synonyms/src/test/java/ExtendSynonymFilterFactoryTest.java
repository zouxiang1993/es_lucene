import com.globalegrow.esearch.plugin.synonyms.ExtendSynonymsFilterPlugin;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.http.HttpTransportSettings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *
 *  File: ExtendSynonymFilterFactoryTest.java
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
@ESIntegTestCase.ClusterScope(
        scope = ESIntegTestCase.Scope.SUITE,
        numDataNodes = 1,
        numClientNodes = 0,
        transportClientRatio = 0,
        supportsDedicatedMasters = false)
public class ExtendSynonymFilterFactoryTest extends ESIntegTestCase {

    @Test
    public void test1() throws Exception {
        System.out.println("启动完成, 等待请求");
        // 等待 HTTP 请求
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    @Override
    protected Settings nodeSettings(int nodeOrdinal) {
        return Settings.builder()
                .put(super.nodeSettings(nodeOrdinal))
                .put(NetworkModule.HTTP_ENABLED.getKey(), true) // 要使用Rest API， 必须启用http模块
                .put(HttpTransportSettings.SETTING_HTTP_PORT.getKey(), 9200) // 端口号
                .put("network.host", "127.0.0.1")
                .build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        ArrayList<Class<? extends Plugin>> plugins = new ArrayList<>();
        plugins.add(Netty4Plugin.class);  // 启用HTTP模块必须要加载这个插件
        plugins.add(ExtendSynonymsFilterPlugin.class); // 加载自定义的插件
        return plugins;
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return nodePlugins();
    }

}
