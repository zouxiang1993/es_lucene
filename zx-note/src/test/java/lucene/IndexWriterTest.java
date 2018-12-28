package lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * <pre>
 *
 *  File: IndexWriterTest.java
 *
 *  Copyright (c) 2018, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2018/12/24				    zouxiang				Initial.
 *
 * </pre>
 */
public class IndexWriterTest {
    private IndexWriter writer;
    private IndexReader reader;

    @Before
    public void setup() throws IOException {
        String path = "E:\\lucene-test\\testWrite";
        // Directory 是数据持久层的抽象接口
        Directory directory = new NIOFSDirectory(Paths.get(path));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setUseCompoundFile(false);
        writer = new IndexWriter(directory, config);
        reader = DirectoryReader.open(writer);
    }

    // 清空索引
    @Test
    public void clearIndex() throws IOException {
        System.out.println("删除前文档数: " + (reader.numDocs() - reader.numDeletedDocs()));
        writer.deleteAll();
        writer.commit();
        reader = DirectoryReader.openIfChanged((DirectoryReader) reader);
        System.out.println("删除后文档数: " + (reader.numDocs() - reader.numDeletedDocs()));
    }

    @Test
    public void testAdd() throws IOException {
        Document doc = new Document();
        doc.add(new StringField("goodsId", "1", Field.Store.YES));
        doc.add(new StringField("price", "10", Field.Store.YES));

        // debug...
        writer.addDocument(doc);

        writer.commit();
        System.out.println("Over...");
    }

    @Test
    public void testUpdate() throws IOException {
        Document doc = new Document();
        doc.add(new StringField("goodsId", "1", Field.Store.YES));
        doc.add(new StringField("price", "1000", Field.Store.YES));

        // debug...
        writer.updateDocument(new Term("goodsId", "1"), doc);

        writer.commit();
        System.out.println("Over...");
    }

    @Test
    public void testDelete() throws IOException {
        // TODO:
    }

    @Test
    public void testGet() throws Exception {
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("goodsId", "1")), 1);
        ScoreDoc doc = topDocs.scoreDocs[0];
        Document document = searcher.doc(doc.doc);
        System.out.println(document);
    }
}
