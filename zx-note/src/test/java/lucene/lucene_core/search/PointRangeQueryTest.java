package lucene.lucene_core.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

public class PointRangeQueryTest {

    @Test
    public void test1() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig();
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, iwc);
        indexWriter.addDocument(createDocument(5));
        indexWriter.addDocument(createDocument(10));
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query query = IntPoint.newRangeQuery("age", 4, 8);
        TopDocs topDocs = searcher.search(query, 5);
        System.out.println("结果总数: " + topDocs.totalHits);
    }

    private Document createDocument(int age) {
        Document document = new Document();
        Field field = new IntPoint("age", age);
        document.add(field);
        return document;
    }
}
