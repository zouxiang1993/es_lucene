import org.junit.Assert;
import org.junit.Test;

/**
 * <pre>
 *
 *  File: LangTest.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/10				    zouxiang				Initial.
 *
 * </pre>
 */
public class LangTest {
    /**
     * 多次赋值。从右往左依次执行。
     * LinkedBlockingQueue 201行:  last = last.next = node;
     */
    @Test
    public void testMultiAssign() {
        int a = 3;
        int b = 2;
        b += a *= 5;
        Assert.assertEquals(a, 15);
        Assert.assertEquals(b, 17);
    }
}
