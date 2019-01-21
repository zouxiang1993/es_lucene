package javas.spi;

import org.junit.Test;

import java.util.ServiceLoader;

/**
 * <pre>
 *
 *  Description:
 *  https://www.cnblogs.com/lovesqcc/p/5229353.html <br>
 *  JavaSPI实际上是"面向接口编程 + 策略模式 + 配置文件" 组合实现的动态加载机制。 <br>
 *  具体实现步骤: <br/>
 *  1. 定义一个接口, javas.spi.ILanguage
 *  2. 提供多个实现, javas.spi.Chinese, javas.spi.English
 *  3. 在src/test/resources/ 下建立 /META_INF/services 目录，并新建一个以接口全名命名的文件"javas.spi.ILanguage",
 *     文件内容是要应用的实现类的全名(javas.spi.Chinese 或 javas.spi.English 或多个一起 <br>
 *  4. 使用java.util.ServiceLoader来加载配置文件中指定的实现。
 *
 *  Lucene的Codec就是使用SPI的机制来指定各种索引文件的格式
 *
 * </pre>
 */
public class JavaSPITest {
    @Test
    public void test() {
        ServiceLoader<ILanguage> serviceLoader = ServiceLoader.load(ILanguage.class);
        ILanguage language = serviceLoader.iterator().next();
        language.show();
    }
}
