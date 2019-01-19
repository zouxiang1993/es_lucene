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
    public void testTermQueryScoring() throws IOException {
        Query query = new TermQuery(new Term("title", "lucene"));
        searchByQuery(query);
    }

    // 多个Term Query
    /*  打分详细解释
1.6569202 = sum of:


  0.66665417 = weight(title:lucene in 1) [BM25Similarity], result of:
    0.66665417 = score(doc=1,freq=1.0 = termFreq=1.0
), product of:
      0.6931472 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        3.0 = docFreq
        6.0 = docCount
      0.96177864 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
        1.0 = termFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        2.3333333 = avgFieldLength
        2.56 = fieldLength


  0.990266 = weight(title:elasticsearch in 1) [BM25Similarity], result of:
    0.990266 = score(doc=1,freq=1.0 = termFreq=1.0
), product of:
      1.0296195 = idf, computed as log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5)) from:
        2.0 = docFreq
        6.0 = docCount
      0.96177864 = tfNorm, computed as (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * fieldLength / avgFieldLength)) from:
        1.0 = termFreq=1.0
        1.2 = parameter k1
        0.75 = parameter b
        2.3333333 = avgFieldLength
        2.56 = fieldLength
     */
    @Test
    public void testBooleanQueryScorering() throws IOException {
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
