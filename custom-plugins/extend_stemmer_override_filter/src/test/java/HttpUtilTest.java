import com.globalegrow.esearch.plugin.esof.HttpUtil;
import org.junit.Test;

/**
 * <pre>
 *
 *  File: HttpUtilTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/2				    zouxiang				Initial.
 *
 * </pre>
 */
public class HttpUtilTest {
    @Test
    public void testSendGet() throws Exception {
        String result = HttpUtil.sendGet("http://localhost:8082/api/gearbest2/biz/getRules/en", null);
        System.out.println(result);
    }
}
