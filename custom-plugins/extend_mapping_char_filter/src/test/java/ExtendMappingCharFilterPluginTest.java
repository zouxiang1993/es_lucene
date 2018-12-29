import com.globalegrow.esearch.plugin.extend_mapping.ExtendMappingCharFilterPlugin;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.http.HttpTransportSettings;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *
 *  File: ExtendMappingCharFilterPluginTest.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/29				    zouxiang				Initial.
 *
 * </pre>
 */
// TODO: 不加这行注解为什么会报错？
@ESIntegTestCase.ClusterScope(
        scope = ESIntegTestCase.Scope.SUITE,
        numDataNodes = 1,
        numClientNodes = 0,
        transportClientRatio = 0,
        supportsDedicatedMasters = false)
public class ExtendMappingCharFilterPluginTest extends ESIntegTestCase{
    private static Path home;
    private static String HOME_PATH;

    @BeforeClass
    public static void myBeforeClass() throws Exception {
        // 当作es的根目录: home.path
        home = createTempDir();
        HOME_PATH = home.toFile().getAbsolutePath();
    }

    @Test
    public void test1() throws Exception {
        // 初始化 config/mappings.txt 文件内容
        Path configDir = home.resolve("config/");
        configDir.toFile().mkdir();
        Path mappingsFile = configDir.resolve("mappings.txt");
        System.out.println("字符映射配置文件位置： " + mappingsFile);
        mappingsFile.toFile().createNewFile();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mappingsFile.toFile())));
        bw.write("a => X\n");
        bw.write("b => Y\n");
        bw.write("c => Z\n");
        bw.close();

        System.out.println("启动完成");

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
                .put(Environment.PATH_HOME_SETTING.getKey(), HOME_PATH)
                .put("network.host", "127.0.0.1")
                .build();
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        ArrayList<Class<? extends Plugin>> plugins = new ArrayList<>();
        plugins.add(Netty4Plugin.class);  // 启用HTTP模块必须要加载这个插件
        plugins.add(ExtendMappingCharFilterPlugin.class); // 加载自定义的插件
        return plugins;
    }

    @Override
    protected Collection<Class<? extends Plugin>> transportClientPlugins() {
        return nodePlugins();
    }

}
