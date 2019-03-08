package lucene.lucene_core.codecs.livedocs;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <pre>
 *
 *  File: LiveDocsTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/15				    zouxiang				Initial.
 *
 * </pre>
 */
public class LiveDocsTest {

    @Test
    public void test1() throws Exception {
        Path path = Paths.get("E:\\lucene-test\\testLiveDocs");
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config.setUseCompoundFile(false);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        FieldType fieldType = new FieldType();
        fieldType.setTokenized(true);
        fieldType.setStored(true);
        fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        fieldType.freeze();

        Document doc0 = new Document();
        doc0.add(new Field("title", "hello lucene, lucene and elasticsearch, lucene and elasticsearch is fast", fieldType));
        indexWriter.addDocument(doc0);

        Document doc1 = new Document();
        doc1.add(new Field("title", "world", fieldType));
        indexWriter.addDocument(doc1);

        Document doc2= new Document();
        doc2.add(new Field("title", "hello world", fieldType));
        indexWriter.addDocument(doc2);

        Document doc3 = new Document();
        doc3.add(new Field("title", "wwwww   bbbb ", fieldType));
        indexWriter.addDocument(doc3);

        Document doc4 = new Document();
        doc4.add(new Field("title", "wwwww   bbbb xxxxx", fieldType));
        indexWriter.addDocument(doc4);

        indexWriter.deleteDocuments(new TermQuery(new Term("title", "world")));   // 删除了 doc1, doc2 , 还剩下doc0, doc3, doc4
        indexWriter.commit();

        IndexReader reader = DirectoryReader.open(indexWriter);
    }
}
