package javas.lang;

import org.junit.Test;

/**
 * <pre>
 *
 *  File: IntegerTest.java
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
public class IntegerTest {
    /**
     * 进制转换
     */
    @Test
    public void testToString() {
        int i = 65535;
        System.out.println(Integer.toString(i, 16));
        System.out.println(Integer.toString(i, 10));
    }

    @Test
    public void testBitOpts() {
        System.out.println(Integer.highestOneBit(65535));
        System.out.println(Integer.lowestOneBit(65535));
        System.out.println(Integer.highestOneBit(0x111000));
        System.out.println(Integer.lowestOneBit(0x111000));

        System.out.println(Integer.numberOfLeadingZeros(65535));
        System.out.println(Integer.numberOfTrailingZeros(65535));

        System.out.println(Integer.bitCount(0x111000));
    }

    @Test
    public void testRotate() {
        int i = 0b10110011100011110000111110000010;
        printBinaryIntWithLeadingZeros(i);
        // 左移三位，溢出的3位补充到最右边
        System.out.println("左移");
        printBinaryIntWithLeadingZeros(Integer.rotateLeft(i, 3));
        // 右移三位，溢出的部分补充到最左边
        System.out.println("右移");
        printBinaryIntWithLeadingZeros(Integer.rotateRight(i, 3));
        System.out.println("反转");
        printBinaryIntWithLeadingZeros(Integer.reverse(i));
    }

    private void printBinaryIntWithLeadingZeros(int num) {
        int leadingZeros = Integer.numberOfLeadingZeros(num);
        for (int i = 0; i < leadingZeros; i++) {
            System.out.print("0");
        }
        System.out.println(Integer.toBinaryString(num));
    }
}
