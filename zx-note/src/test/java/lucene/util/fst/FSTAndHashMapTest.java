package lucene.util.fst;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.Builder;
import org.apache.lucene.util.fst.FST;
import org.apache.lucene.util.fst.PositiveIntOutputs;
import org.apache.lucene.util.fst.Util;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * <pre>
 *
 *  File: FSTAndHashMapTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  比较FST和HashMap的内存占用和查询速度
 *  <pre>
 *  使用resources/lucene/util/fst/dic.txt中的132762个<英文单词, 随机正整数>键值对：
 *  FST占用内存1194KB， HashMap占用内存19682KB。HashMap的内存使用量约为FST的16倍
 *  在上述基础上使用1000万个存在的key来查询：
 *  FST耗时约12500ms， HashMap耗时约1300ms， FST的查询耗时大约位HashMap的10倍。
 * </pre>
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/10				    zouxiang				Initial.
 *
 * </pre>
 */
public class FSTAndHashMapTest {
    /**
     * 测试FST的内存占用
     *
     * @throws IOException
     */
    @Test
    public void testFSTRamUsage() throws IOException {
        FST fst = buildFST();
        long ramBytesUsed = fst.ramBytesUsed();
        System.out.println(ramBytesUsed + "B"); // 1223360B
        System.out.println(ramBytesUsed / 1024 + "KB"); // 1194KB
    }

    /**
     * 测试HashMap的内存占用<br/>
     * 在jvisualvm中查看堆dump，并使用下面的OQL查找出size最大的HashMap对象
     * <pre>
     * select map(
     * 	top(
     * 		heap.objects(
     * 			'java.util.HashMap',
     * 			false
     * 		),
     * 		'rhs.size - lhs.size',
     * 		10
     * 	),
     * 	"toHtml(it) + '  ' + it.size"
     * );
     * </pre>
     * 然后再在实例页面查看该对象的retained size(保留大小) =  20155164B = 19682KB <br/>
     * 这个HashMap的内存占用约为FST的16倍
     *
     * @throws IOException
     */
    @Test
    public void testHashMapRamUsage() throws Exception {
        HashMap<String, Long> map = buildHashMap();
        System.out.println(map.size());
        Field field = HashMap.class.getDeclaredField("table");
        field.setAccessible(true);
        Object[] table = (Object[]) field.get(map);
        // size=132762, capacity=262144, size/capacity=0.50644684
        System.out.printf("size=%s, capacity=%s, size/capacity=%s\n", map.size(), table.length, map.size() / (table.length * 1.0f));
        try {
            new CountDownLatch(1).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试FST的查询性能
     * <p>
     * 1000万次查询约12000ms
     *
     * @throws IOException
     */
    @Test
    public void testFSTQueryPerformance() throws IOException {
        FST fst = buildFST();
        List<String> queryList = generateQueryKey();
        long start = System.currentTimeMillis();
        IntsRefBuilder scatcher = new IntsRefBuilder();
        for (String query : queryList) {
            BytesRef bytesRef = new BytesRef(query);
            Util.get(fst, Util.toIntsRef(bytesRef, scatcher));
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时: " + (end - start) + "ms");
    }

    /**
     * 测试HashMap的查询性能
     * <p>
     * 1000万次查询约1300ms
     *
     * @throws IOException
     */
    @Test
    public void testHashMapPreformance() throws IOException {
        HashMap<String, Long> map = buildHashMap();
        List<String> queryList = generateQueryKey();
        long start = System.currentTimeMillis();
        for (String query : queryList) {
            map.get(query);
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时: " + (end - start) + "ms");
    }

    private FST buildFST() throws IOException {
        Scanner scanner = getFileScanner("dic.txt");
        Builder<Long> builder = new Builder(FST.INPUT_TYPE.BYTE1, PositiveIntOutputs.getSingleton());
        IntsRefBuilder intsRefBuilder = new IntsRefBuilder();
        String line = null;
        while (scanner.hasNextLine()) {
            try {
                line = scanner.nextLine();
                String[] words = line.split("\\s");
                final String key = words[0];
                final Long value = Long.parseLong(words[1]);
                BytesRef bytesRef = new BytesRef(key);
                IntsRef intsRef = Util.toIntsRef(bytesRef, intsRefBuilder);
                builder.add(intsRef, value);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(line);
            }
        }
        FST<Long> fst = builder.finish();
        return fst;
    }

    private HashMap<String, Long> buildHashMap() throws IOException {
        Scanner scanner = getFileScanner("dic.txt");
        HashMap<String, Long> map = new HashMap(); // 调整hashmap容量，也可以节省一点空间，但是也会减慢查询速度。但是压缩空间的效果不明显。
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] words = line.split("\\s");
            final String key = words[0];
            final Long value = Long.parseLong(words[1]);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 随机生成查询的Key
     *
     * @return
     * @throws IOException
     */
    private List<String> generateQueryKey() throws IOException {
        Scanner scanner = getFileScanner("dic.txt");
        List<String> keyList = new ArrayList<>(150000);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String key = line.split("\\s")[0];
            keyList.add(key);
        }
        final int count = 10000000;
        List<String> queryList = new ArrayList<>();
        Random random = new Random(1); // seed保证HashMap和FST用的是相同的随机Query序列
        for (int i = 0; i < count; i++) {
            queryList.add(keyList.get(random.nextInt(keyList.size())));
        }
        return queryList;
    }

    private Scanner getFileScanner(String fileName) throws IOException {
        String path = FSTAndHashMapTest.class.getResource(fileName).getPath();
        return new Scanner(new File(path));
    }

}
