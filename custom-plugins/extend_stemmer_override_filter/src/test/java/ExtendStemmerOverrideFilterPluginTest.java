import com.globalegrow.esearch.plugin.esof.ExtendStemmerOverrideFilterPlugin;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.http.HttpTransportSettings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *
 *  File: ExtendStemmerOverrideFilterPluginTest.java
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
@ESIntegTestCase.ClusterScope(
        scope = ESIntegTestCase.Scope.SUITE,
        numDataNodes = 1,
        numClientNodes = 0,
        transportClientRatio = 0,
        supportsDedicatedMasters = false)
public class ExtendStemmerOverrideFilterPluginTest extends ESIntegTestCase {

    private IndicesAdminClient indicesAdminClient;

    @Before
    public void setup() throws Exception {
        indicesAdminClient = client().admin().indices();
    }

    @Test
    public void test1() throws Exception {
        System.out.println("启动完成, 等待请求");
        // 等待 HTTP 请求
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();

//        assertSingleTermCorrenctStemming("ear", "ear");
//        assertSingleTermCorrenctStemming("earrings", "earring");
//        assertSingleTermCorrenctStemming("earring", "earring");
//        assertSingleTermCorrenctStemming("micro", "micro");
    }

    public void assertSingleTermCorrenctStemming(final String input, final String expectedOutput) throws Exception {
        AnalyzeRequestBuilder requestBuilder = indicesAdminClient.prepareAnalyze();
        AnalyzeResponse response = requestBuilder
                .setText(input)
                .setTokenizer("standard")    // tokenizer必须设置
                .addTokenFilter("extend_stemmer_override")
                .addTokenFilter("porter_stem")
                .execute()
                .get();
        Iterator<AnalyzeResponse.AnalyzeToken> iterator = response.iterator();
        assertTrue("response must have one token", iterator.hasNext());
        System.out.println(input + "------>" + iterator.next().getTerm());
//        assertEquals("response must equals :" + expectedOutput, expectedOutput, iterator.next().getTerm());
        assertTrue("response must have only one token", !iterator.hasNext());
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
        plugins.add(ExtendStemmerOverrideFilterPlugin.class); // 加载自定义的插件
        return plugins;
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return nodePlugins();
    }
}
