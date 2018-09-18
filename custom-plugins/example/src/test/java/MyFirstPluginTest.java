import com.globalegrow.es.plugin.example.MyFirstPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.test.ESIntegTestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *
 *  File: MyFirstPluginTest.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/8/10				    zouxiang				Initial.
 *
 * </pre>
 */
public class MyFirstPluginTest extends ESIntegTestCase {

    @Test
    public void test1() {
        System.out.println("哈哈哈");
        // 永久等待，观察插件行为
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Collection<Class<? extends Plugin>> nodePlugins() {
        Collection<Class<? extends Plugin>> superPlugins = super.nodePlugins();
        List<Class<? extends Plugin>> plugins = new ArrayList<>(superPlugins);
        // 在这里添加自定义插件
        plugins.add(MyFirstPlugin.class);
        return plugins;
    }
}
