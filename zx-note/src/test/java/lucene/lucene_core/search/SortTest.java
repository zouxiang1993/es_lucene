package lucene.lucene_core.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import java.io.IOException;

/**
 * Lucene是利用doc values来进行字段排序的
 */
public class SortTest {
    /**
     * @see TopFieldCollector
     * @see FieldComparator
     */
    @Test
    public void test1() throws IOException {
        Directory directory = new RAMDirectory();
        IndexWriterConfig iwc = new IndexWriterConfig();
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(directory, iwc);
        indexWriter.addDocument(createDocument("xxx", 1));
        indexWriter.addDocument(createDocument("xxx", 2));
        indexWriter.addDocument(createDocument("xxx", 5));
        indexWriter.addDocument(createDocument("xxx", 9));
        indexWriter.addDocument(createDocument("yyy", 9));
        IndexReader reader = DirectoryReader.open(indexWriter);
        IndexSearcher searcher = new IndexSearcher(reader);
        Sort sort = new Sort();
        sort.setSort(new SortField("age", SortField.Type.INT, true));
        TopFieldDocs topFieldDocs = searcher.search(new TermQuery(new Term("name", "xxx")), 5, sort);
        System.out.println("总数: " + topFieldDocs.totalHits);
        for (ScoreDoc scoreDoc : topFieldDocs.scoreDocs) {
            FieldDoc fieldDoc = (FieldDoc) scoreDoc;
            System.out.println("文档ID: " + fieldDoc.doc + ", 字段值= " + fieldDoc.fields[0]);
        }
    }

    private Document createDocument(String name, int age) {
        Document document = new Document();
        document.add(new TextField("name", name, Field.Store.YES));
        document.add(new NumericDocValuesField("age", age));
        return document;
    }
}
