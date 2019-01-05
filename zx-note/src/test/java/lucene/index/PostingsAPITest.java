package lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * <pre>
 *
 *  File: PostingsAPITest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/1/5				    zouxiang				Initial.
 *
 * </pre>
 */
public class PostingsAPITest {
    private IndexReader reader;

    @Before
    public void setup() {
        try {
            Directory directory = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            Document doc1 = new Document();
            doc1.add(new TextField("title", new StringReader("hello world")));
            indexWriter.addDocument(doc1);

            Document doc2 = new Document();
            doc2.add(new TextField("title", new StringReader("hello kitty")));
            indexWriter.addDocument(doc2);

            Document doc3 = new Document();
            doc3.add(new TextField("title", new StringReader("lucene and elasticsearch")));
            indexWriter.addDocument(doc3);

            indexWriter.commit();

            reader = DirectoryReader.open(indexWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() throws IOException {
        System.out.println(reader.getDocCount("title"));
    }

    @Test
    public void test2() {

    }
}
