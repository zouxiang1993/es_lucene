package lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
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
    @Test
    public void testWrite() throws IOException {
        String path = "E:\\lucene-test\\testWrite";
        // Directory 是数据持久层的抽象接口
        Directory directory = new NIOFSDirectory(Paths.get(path));

        IndexWriterConfig config = new IndexWriterConfig();
        config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        config.setUseCompoundFile(false);
        IndexWriter writer = new IndexWriter(directory, config);
        Document doc = new Document();
        doc.add(new StringField("author", "zx", Field.Store.YES));

        writer.addDocument(doc);

        writer.commit();

        System.out.println("Over...");
    }

}
