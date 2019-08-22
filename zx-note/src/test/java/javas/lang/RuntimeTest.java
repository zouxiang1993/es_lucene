package javas.lang;

import org.junit.Test;

public class RuntimeTest {
    @Test
    public void test1() {
        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        System.out.println(freeMemory / 1024 / 1024);  // 当前jvm剩余内存
        System.out.println(totalMemory / 1024 / 1024);  // 当前jvm占用内存
        System.out.println(maxMemory / 1024 / 1024);  // jvm最多占用操作系统多少内存
    }
}
