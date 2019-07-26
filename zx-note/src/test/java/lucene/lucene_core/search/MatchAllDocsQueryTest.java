package lucene.lucene_core.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * <pre>
 *
 *  File: MatchAllDocsQueryTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/7/25				    zouxiang				Initial.
 *
 * </pre>
 */
public class MatchAllDocsQueryTest {
    private FieldType fieldType;

    @Before
    public void before() {
        fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS);
        fieldType.setStored(true);
        fieldType.freeze();
    }

    @Test
    public void test1() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig();
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, iwc);
        indexWriter.addDocument(createDocument());
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);
        Query query = new MatchAllDocsQuery();
        TopDocs topDocs = searcher.search(query, 5);
        System.out.println("结果总数: " + topDocs.totalHits);
    }

    private Document createDocument() {
        Document document = new Document();
        Field field = new Field("goodsTitle", "bikini", fieldType);
        document.add(field);
        return document;
    }
}
