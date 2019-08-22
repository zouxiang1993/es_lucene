package javas.lang;

import org.junit.Test;

import java.util.Map;
import java.util.Properties;

/**
 * <pre>
 *
 *  File: SystemTest.java
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
public class SystemTest {
    @Test
    public void testProperties() {
        Properties properties = System.getProperties();
        for (Object obj : properties.keySet()) {
            System.out.println(obj + "\t" + properties.get(obj));
        }
    }

    @Test
    public void testEnv() {
        Map<String, String> env = System.getenv();
        for (Object obj : env.keySet()) {
            System.out.println(obj + "\t" + env.get(obj));
        }
    }
}
