package javas.lang;

import org.junit.Test;

/**
 * <pre>
 *
 *  File: StringTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/8/22				    zouxiang				Initial.
 *
 * </pre>
 */
public class StringTest {
    @Test
    public void testHash() {
        String s = "aaaaaaaaaaaaaaaaaaaaaa";
        System.out.println(s.hashCode());
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
            System.out.println(h);
        }
    }

    /*
    什么时候会在常量池存储字符串对象?

    1. 显式调用String.intern方法的时候
    2. 直接生命字符串字面常量的时候。例如: String a = "aaa"
    3. 字符串直接常量相加的时候。例如: String c = "aa" + "bb",
        其中的"aa"/"bb"只要有任何一个不是字符串字面常量形式，都不会在常量池生成"aabb",
        且此时jvm做了优化，不会在字符串常量池中生成"aa"和"bb"
     */

    /*
    1. String s = "11"
        从常量池中加载字符串，如果不存在，则会先放入常量池
        s指向常量池中的对象

    2. String s1 = new String("11")
        在堆上new一个对象，
        s1指向新new出来的对象
        将new出来的字符串放到常量池中
     */
    @Test
    public void test1() {
        String s1 = new String("aaa");
        String s2 = "aaa";
        System.out.println(s1 == s2); // false

        s1 = new String("bbb").intern();
        s2 = "bbb";
        System.out.println(s1 == s2);  // true

        s1 = "ccc";
        s2 = "ccc";
        System.out.println(s1 == s2); // true

        s1 = new String("ddd");
        s2 = new String("ddd");
        System.out.println(s1 == s2); // false

        s1 = new String("eee").intern();
        s2 = new String("eee").intern();
        System.out.println(s1 == s2); // true
    }

    @Test
    public void test2() {
        String s1 = "ab" + "cd";
        String s2 = "abcd";
        System.out.println(s1 == s2); // true

        String temp = "hh";
        s1 = "a" + temp;
        s2 = "ahh";
        System.out.println(s1 == s2);  // false  为什么?
    }

    @Test
    public void test() {
        String a = "1";
        for (int i = 0; i < 10; i++) {
            a += i; // 每次循环都要new一个StringBuilder对象, 对性能有损耗
        }

        // 优化成:
        StringBuilder sb = new StringBuilder("1");
        for (int i = 0; i < 10; i++) {
            sb.append("1");
        }
    }

}
