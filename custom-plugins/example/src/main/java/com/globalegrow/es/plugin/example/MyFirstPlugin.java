package com.globalegrow.es.plugin.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.plugins.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <pre>
 *
 *  File: MyFirstPlugin.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/8/10				    zouxiang				Initial.
 *
 * </pre>
 */
public class MyFirstPlugin extends Plugin {
    private final static Logger LOG = LogManager.getLogger();
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public MyFirstPlugin() {
        super();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String dateString = sdf.format(new Date());
                LOG.info("Hello World! Now: " + dateString);
            }
        }, 10000, 10000);
    }
}
