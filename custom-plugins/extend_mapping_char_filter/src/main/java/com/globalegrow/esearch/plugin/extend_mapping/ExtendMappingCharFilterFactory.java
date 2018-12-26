package com.globalegrow.esearch.plugin.extend_mapping;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;
import org.elasticsearch.index.analysis.MappingCharFilterFactory;

import java.io.File;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Locale;

/**
 * <pre>
 *
 *  File: ExtendMappingCharFilterFactory.java
 *
 *  Description:
 *  Lucene自带的MappingCharFilter，提供了通过"mappings_path"来配置映射文件的位置。
 *  目前的需求：zaful有很多国家站，只有个别站比如ZFFR 需要配置mapping
 *  国家站setting均为 "mappings_path" : "char_mapping/{domain}.txt"
 *  但是如果mappings_path配置的文件不存在，则建索引时会报错。
 *  这个插件就是用来解决这个问题。
 *  当文件不存在时，则此CharFilter不做任何处理
 *  当文件存在时，创建一个MappingCharFilter
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/26				    zouxiang				Initial.
 *
 * </pre>
 */
public class ExtendMappingCharFilterFactory extends AbstractCharFilterFactory {

    private static final String MAPPINGS_PATH = "mappings_path";
    private static Logger logger = ESLoggerFactory.getLogger("extend-mapping");
    Environment environment;
    private Settings settings;
    // factory==null表示不做任何处理, factory!=null表示用factory.create(..)来处理
    private final MappingCharFilterFactory factory;


    public ExtendMappingCharFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name);
        this.environment = environment;
        this.settings = settings;
        String wordListPath = this.settings.get(MAPPINGS_PATH, null);
        if (wordListPath == null || wordListPath.trim().length() == 0) {
            String message = String.format(Locale.ROOT, "%s param is required.", MAPPINGS_PATH);
            throw new IllegalArgumentException(message);
        }
        final Path path = environment.configFile().resolve(wordListPath);

        if (path == null || !new File(path.toString()).exists()) {
            // 如果文件不存在，则不做任何处理
            logger.warn("index: {}, file: {} is not exist, use dummy CharFilter instead.", indexSettings.getIndex().getName(), path);
            factory = null;
        } else {
            // 如果文件存在，则
            logger.info("index: {}, file: {} is found", indexSettings.getIndex().getName(), path);
            factory = new MappingCharFilterFactory(indexSettings, environment, name(), settings);
        }
    }

    @Override
    public Reader create(Reader tokenStream) {
        if (factory == null) {
            return tokenStream;
        } else {
            return factory.create(tokenStream);
        }
    }
}
