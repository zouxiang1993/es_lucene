package com.globalegrow.esearch.plugin.stanfordstem;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

import java.io.IOException;

/**
 * <pre>
 *
 *  File: StanfordStemFilterFactory.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/19				    zouxiang				Initial.
 *
 * </pre>
 */
public class StanfordStemFilterFactory extends AbstractTokenFilterFactory {

    public StanfordStemFilterFactory(IndexSettings indexSettings, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StanfordStemFilter(tokenStream);
    }
}
