package javas.util;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * HashMap内部使用 Node<K,V>[] table 作为hash表，table的长度始终为2的幂,假设记作2^x <br/>
 * 对于插入的每一个键值对Entry(Key,Value), 使用Key的hashCode()的最后x位作为它在table中的偏移量。 <br/>
 * 当发生hash冲突时，采用链式存储的方法来存储冲突的节点。 <br/>
 * 当一条链上的节点数超过HashMap.TREEIFY_THRESHOLD(默认为8)时，改用树状结构来存储冲突的节点。 <br/>
 * 如果Key实现了Comparable接口，那么树还是有序的。<br/>
 * <br/>
 * HashMap有两个重要的属性: capacity 和 load factor。 <br/>
 * capacity是容量，也就是table[]的长度，也可以称为hash桶的数目。<br/>
 * load factor 是负载因子，表示HashMap可以装多满<br/>
 * 当Entry数目超过 capacity*load factor之后，hash表就会进行rehash，
 * 重建内部的数据结构，这个过程会将桶数目调整至原来的2倍。<br/>
 * 一般情况下，默认的load factor(0.75)可以在时间和空间上取得一个很好的平衡。<br/>
 * 过高的值会减少空间消耗，但会增加时间消耗 <br/>
 * <br/>
 * 在设置初始容量时，需要考虑"要插入的Entry总数"。<br/>
 * 如果 初始容量 > 负载因子*要插入的Entry总数 , 那么就可以避免rehash操作。
 */
public class HashMapTest {
    /**
     * HashMap的table字段延迟构造，且长度始终为2的幂
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        System.out.println("开始");
        HashMap<Integer, Integer> map = new HashMap(10, 0.75f);

        System.out.println(tableCapacity(map));  // 此时，arr为null

        map.put(1, 111); // 触发table的初始化

        System.out.println(tableCapacity(map)); // 此时, arr为长度为16的数组
    }

    /**
     * 自动扩容
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        System.out.println("开始");
        HashMap<Integer, Integer> map = new HashMap(8, 0.5f);

        for (int i = 1; i < 10; i++) {
            map.put(i, i * 11);
            System.out.printf("插入:<%s, %s>\r\n", i, i * 11);
            System.out.println("map容量:" + tableCapacity(map));
        }
    }

    /**
     * 解决hash冲突- 链表存储
     * <p>
     * 观察hash表中的第一个节点，以链表形式链接了8个节点。
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        System.out.println("开始");
        HashMap<MyKey, Integer> map = new HashMap(64, 0.5f);
        for (int i = 1; i <= 8; i++) {
            map.put(new MyKey(i), i);
        }
        System.out.println(map);
    }

    /**
     * 解决hash冲突- 树状存储
     * <p>
     * 观察hash表中的第一个节点，以TreeNode形式存储了9个节点。
     * <p>
     * 当一个位置上存储超过了HashMap.TREEIFY_THRESHOLD(默认为8)个元素时，改为树状存储
     * <p>
     * 但是由于MyKey没有实现Comparable接口，Tree是无序的。
     *
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        System.out.println("开始");
        HashMap<MyKey, Integer> map = new HashMap(64, 0.5f);
        for (int i = 1; i <= 9; i++) {
            map.put(new MyKey(i), i);
        }
        System.out.println(map);
    }

    /**
     * 由于MyComparableKey实现了Comparable接口，因此此时的树是有序的。
     *
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        System.out.println("开始");
        HashMap<MyComparableKey, Integer> map = new HashMap(64, 0.5f);
        for (int i = 1; i <= 9; i++) {
            map.put(new MyComparableKey(i), i);
        }
        System.out.println(map);
    }

    static class MyKey {
        int actualKey;

        public MyKey(int key) {
            this.actualKey = key;
        }

        @Override
        public int hashCode() {
            return 0; // 所有对象的hashCode全部相同
        }

        @Override
        public boolean equals(Object obj) {
            return false;  // 都不相等
        }
    }

    static class MyComparableKey extends MyKey implements Comparable<MyComparableKey> {

        public MyComparableKey(int key) {
            super(key);
        }

        @Override
        public int compareTo(MyComparableKey other) {
            return this.actualKey - other.actualKey;
        }
    }

    private static final Field f;

    static {
        Field field;
        try {
            field = HashMap.class.getDeclaredField("table");
            field.setAccessible(true);
        } catch (Exception e) {
            field = null;
        }
        f = field;
    }

    private Integer tableCapacity(HashMap map) {
        try {
            Object table = f.get(map);
            if (table == null) {
                return null;
            } else {
                return ((Object[]) table).length;
            }
        } catch (Exception e) {
            throw new RuntimeException("should never be here");
        }
    }

    @Test
    public void test() {
        int hash = 0b10101010101010101010101;
        System.out.println(Integer.toBinaryString(hash));
        System.out.println(Integer.toBinaryString(hash >>> 16));
        int h;
        System.out.println(Integer.toBinaryString((h=hash) ^ (h >>> 16)));
    }
}
