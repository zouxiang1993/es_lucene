package lucene.lucene_core.codecs.termvectors;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * <pre>
 *
 *  File: TermVectorsTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/14				    zouxiang				Initial.
 *
 * </pre>
 */
public class TermVectorsTest {
    private IndexReader reader;

    @Before
    public void setup() {
        try {
            Directory directory = new RAMDirectory();
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            config.setUseCompoundFile(false);
            IndexWriter indexWriter = new IndexWriter(directory, config);

            FieldType fieldType = new FieldType();
            fieldType.setTokenized(true);
            fieldType.setStored(true);
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
//            fieldType.setOmitNorms(true);

            // 存储Term Vector
            fieldType.setStoreTermVectors(true);
            fieldType.setStoreTermVectorPositions(true);
            fieldType.setStoreTermVectorOffsets(true);

            fieldType.freeze();

            Document doc1 = new Document();
            doc1.add(new Field("title", "hello lucene, lucene and elasticsearch, lucene and elasticsearch is fast", fieldType));
            indexWriter.addDocument(doc1);

//            Document doc2 = new Document();
//            doc1.add(new Field("title", "hello lucene, lucene and elasticsearch, lucene and elasticsearch is fast", fieldType));
//            indexWriter.addDocument(doc2);

            reader = DirectoryReader.open(indexWriter);

            String[] files = directory.listAll();
            System.out.println("当前目录下的所有文件：");
            for (String file : files) {
                System.out.println(file);
            }
            System.out.println("-----------------------");
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Term Vectors API的使用
     * <p>
     * Term Vectors和倒排索引存储的内容很相似,
     * 但倒排索引适用于根据term查询，term vectors适用于根据文档id查询。
     *
     * @throws Exception
     */
    @Test
    public void testTermVectorsAPI() throws Exception {
        SegmentReader segmentReader = (SegmentReader) (reader.leaves().get(0).reader());
        Fields termVectors = segmentReader.getTermVectors(0); // 获取docID=0的文档的term vectors
        Terms titleTermVectors = termVectors.terms("title"); // 获取title字段的term vectors

        TermsEnum termsEnum = titleTermVectors.iterator();
        BytesRef bytesRef;
        for (bytesRef = termsEnum.next(); ; bytesRef = termsEnum.next()) { // 顺序遍历每一个term
            if (bytesRef == null) {
                break;
            }
            PostingsEnum docs = termsEnum.postings(null); // 该term的倒排表
            docs.nextDoc(); // 倒排表的第一项，也是仅有的一项。
            int freq = docs.freq(); // term在文档中出现的频次
            System.out.println(bytesRef.utf8ToString() + "\t" + freq);

            for (int i = 0; i < freq; i++) {
                int position = docs.nextPosition();
                int startOffset = docs.startOffset();
                int endOffset = docs.endOffset();
                System.out.println(position + "\t" + startOffset + "\t" + endOffset);
            }
        }
    }
}
