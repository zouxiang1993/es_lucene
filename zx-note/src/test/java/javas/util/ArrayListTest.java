package javas.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *
 *  File: ArrayListTest.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/11				    zouxiang				Initial.
 *
 * </pre>
 */
public class ArrayListTest {
    /**
     * 删除一段元素
     */
    @Test
    public void testRemoveRange() {
        ArrayList list = new ArrayList(10);
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        list.trimToSize();
        print(list);
        list.subList(3, 6).clear();
        print(list);
    }

    @Test
    public void testGetIndexInRange() {
        ArrayList list = new ArrayList();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                list.add(j);
            }
        }
        print(list);
        System.out.println(list.indexOf(3));
        System.out.println(list.subList(7, 15).indexOf(3)+7);
    }


    private void print(List list) {
        if (list == null) {
            System.out.println("null");
        } else {
            for (Object obj : list) {
                System.out.print(obj + "\t");
            }
            System.out.println();
        }
    }
}
