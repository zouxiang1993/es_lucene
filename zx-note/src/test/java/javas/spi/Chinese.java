package javas.spi;

/**
 * <pre>
 *
 *  File: Chinese.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/1/21				    zouxiang				Initial.
 *
 * </pre>
 */
public class Chinese implements ILanguage {
    @Override
    public void show() {
        System.out.println("中文...");
    }
}
