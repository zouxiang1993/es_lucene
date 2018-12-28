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

//    @Before
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

    // 添加文档 数据路径
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

    // 更新文档 数据路径
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

    // 删除文档 数据路径
    @Test
    public void testDelete() throws IOException {
        // TODO:
    }

    /**
     * 测试 Flush 和 Commit
     * Flush是将DWPT内的In-memory buffer中的数据持久化到文件的过程。
     * 每个DWPT会flush成一个segment（xxx.si表示的一个segment以及其他一些数据文件）。
     * flush完成后，这个segment还是不可被搜索的，只有在commit之后才能被搜索。
     * commit时会触发一次强制flush，commit会生成一个commit point(一个新的segnets_N文件)。
     * Commit point会由IndexDeletionPolicy管理，lucene默认的策略是只会保留最后一个commit point.
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void testFlushAndCommit() throws IOException, InterruptedException {
        // 运行此方法前，先注释掉setup()方法
        String path = "E:\\lucene-test\\testWrite";
        // Directory 是数据持久层的抽象接口
        Directory directory = new NIOFSDirectory(Paths.get(path));
        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setUseCompoundFile(false);
        config.setIndexDeletionPolicy(NoDeletionPolicy.INSTANCE);   // 不删除commit point (segment_N文件)
        writer = new IndexWriter(directory, config);
        reader = DirectoryReader.open(writer);
        // 首先清除目录下所有的索引文件
        Document doc = new Document();
        doc.add(new StringField("goodsId", "1", Field.Store.YES));
        doc.add(new StringField("price", "10", Field.Store.YES));

        writer.addDocument(doc);

        writer.flush();

        writer.commit();

        Document doc2 = new Document();
        doc2.add(new StringField("goodsId", "2", Field.Store.YES));
        doc2.add(new StringField("price", "100", Field.Store.YES));

        writer.addDocument(doc2);

        writer.flush();

        writer.commit();

        System.out.println("Over...");
    }

    // 辅助用，查看索引中文档内容
    @Test
    public void testGet() throws Exception {
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(new TermQuery(new Term("goodsId", "1")), 1);
        ScoreDoc doc = topDocs.scoreDocs[0];
        Document document = searcher.doc(doc.doc);
        System.out.println(document);
    }
}
