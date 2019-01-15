package lucene.search;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <pre>
 *
 *  File: SearchTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  查询流程 & 打分流程
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/1/14				    zouxiang				Initial.
 *
 * </pre>
 */
public class SearchTest {

    private final String[] TITLES = new String[]{
            "hello lucene",
            "lucene and elasticsearch",
            "lucene 9 is comming",
            "hello world",
            "haha hehe xixi",
            "java and elasticsearch"
    };

    // 单个Term Query
    // debug IndexSearcher.createNormalizedWeight
    @Test
    public void testTermQuery() throws IOException {
        Query query = new TermQuery(new Term("title", "lucene"));
        searchByQuery(query);
    }

    // 多个Term Query
    @Test
    public void testBM25Similarity() throws IOException {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        BooleanQuery query = builder.setDisableCoord(false)
                .add(new BooleanClause(new TermQuery(new Term("title", "lucene")), BooleanClause.Occur.SHOULD))
                .add(new BooleanClause(new TermQuery(new Term("title", "elasticsearch")), BooleanClause.Occur.SHOULD))
                .build();
        searchByQuery(query);
    }

    private void searchByQuery(Query query) throws IOException {
        Path path = Paths.get("E:\\lucene-test\\testScore");
        Directory directory = FSDirectory.open(path);
        IndexWriterConfig config = new IndexWriterConfig()
                .setUseCompoundFile(false)
                .setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, config);
        for (String title : TITLES) {
            Document doc = new Document();
            doc.add(new TextField("title", title, Field.Store.YES));
            writer.addDocument(doc);
        }
        writer.commit();

        IndexReader reader = DirectoryReader.open(writer);
        IndexSearcher searcher = new IndexSearcher(reader);

        TopDocs topDocs = searcher.search(query, 10);
        System.out.println("结果总数: " + topDocs.totalHits);
        for (ScoreDoc doc : topDocs.scoreDocs) {
            int docID = doc.doc;
            Document document = searcher.doc(docID);
            String title = document.get("title");
            System.out.printf("得分: %s, docID: %s, 标题: %s\n", doc.score, docID, title);
        }
        int firstDocID = topDocs.scoreDocs[0].doc;
        Explanation explanation = searcher.explain(query, firstDocID);
        System.out.println(explanation);
    }
}
