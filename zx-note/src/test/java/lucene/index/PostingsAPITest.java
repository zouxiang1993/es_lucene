package lucene.index;

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
 *  File: PostingsAPITest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  Term & 倒排表 API
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

            FieldType fieldType = new FieldType();
            fieldType.setTokenized(true);
            fieldType.setStored(true);
            fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
            fieldType.freeze();

            Document doc1 = new Document();
            doc1.add(new Field("title", "hello world", fieldType));
            indexWriter.addDocument(doc1);

            Document doc2 = new Document();
            doc2.add(new Field("title", "hello kitty", fieldType));
            indexWriter.addDocument(doc2);

            Document doc3 = new Document();
            doc3.add(new Field("title", "lucene and elasticsearch", fieldType));
            indexWriter.addDocument(doc3);

            Document doc4 = new Document();
            doc4.add(new Field("title", "what lucene when lucene how lucene", fieldType));
            indexWriter.addDocument(doc4);
            indexWriter.commit();

            reader = DirectoryReader.open(indexWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Terms API
    @Test
    public void testTermsAPI() throws IOException {
        LeafReader leafReader = this.reader.leaves().get(0).reader();
        Terms terms = leafReader.terms("title");
        String min = terms.getMin().utf8ToString();
        System.out.println("最小值: " + min);
        String max = terms.getMax().utf8ToString();
        System.out.println("最大值: " + max);
        System.out.println("是否有freqs: " + terms.hasFreqs());
        System.out.println("是否有position: " + terms.hasPositions());
        System.out.println("是否有offset: " + terms.hasOffsets());
        System.out.println("是否有payloads: " + terms.hasPayloads());
        System.out.println("Term总数: " + terms.size());
        System.out.println("docCount: " + terms.getDocCount());
        System.out.println(terms.getSumDocFreq());
        System.out.println(terms.getSumTotalTermFreq());
    }

    // TermsEnum API
    @Test
    public void testTermsEnumAPI() throws IOException {
        LeafReader leafReader = this.reader.leaves().get(0).reader();
        Terms terms = leafReader.terms("title");
        TermsEnum termsEnum = terms.iterator();
        while (termsEnum.next() != null) {
//            long ord = termsEnum.ord();
            String term = termsEnum.term().utf8ToString();
            int docFreq = termsEnum.docFreq();
            long totalTermFreq = termsEnum.totalTermFreq();
            PostingsEnum postings = termsEnum.postings(null);
            System.out.println(term + "\t" + docFreq + "\t" + totalTermFreq);
        }
    }

    // PostingsEnum API
    @Test
    public void testPostingsEnumAPI() throws IOException {
        LeafReader leafReader = this.reader.leaves().get(0).reader();
        Terms terms = leafReader.terms("title");
        TermsEnum termsEnum = terms.iterator();
        boolean found = termsEnum.seekExact(new BytesRef("lucene"));
        if (!found) {
            throw new RuntimeException("should never get here!");
        }
        PostingsEnum postingsEnum = termsEnum.postings(null);
        while (postingsEnum.nextDoc() != PostingsEnum.NO_MORE_DOCS) {
            int docID = postingsEnum.docID();
            int freq = postingsEnum.freq();
            System.out.println("文档ID: " + docID + ", 词频:" + freq);
            for (int i = 0; i < freq; i++) {
                // TODO: 怎么才能获取准确的位置信息？？？
                int position = postingsEnum.nextPosition();
                int startOffset = postingsEnum.startOffset();
                int endOffset = postingsEnum.endOffset();
                System.out.printf("\tposition:%s, startOffset:%s, endOffset:%s\n", position, startOffset, endOffset);
            }
        }
    }
}
