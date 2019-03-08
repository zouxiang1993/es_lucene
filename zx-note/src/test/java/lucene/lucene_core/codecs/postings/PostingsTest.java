package lucene.lucene_core.codecs.postings;

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
 *  File: PostingsTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/2/13				    zouxiang				Initial.
 *
 * </pre>
 */
public class PostingsTest {
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

    /**
     * 断点调试，主要观察BlockTreeTermsReader类的构造方法。
     * 其中包含了.tip文件的读取过程以及.tim文件的部分读取过程。
     *
     * @throws Exception
     */
    @Test
    public void test1() throws Exception {
        System.out.println("哈哈");
    }

    @Test
    public void test2() throws Exception {
        SegmentReader segmentReader = (SegmentReader) (reader.leaves().get(0).reader());
        Fields fields = segmentReader.fields();
        Terms terms = fields.terms("title");
        TermsEnum termsEnum = terms.iterator();
        boolean find = termsEnum.seekExact(new BytesRef("lucene"));
        System.out.println("找到了吗？" + find);
        int docFreq = termsEnum.docFreq(); // 文档频率: term在几个文档中出现过？
        PostingsEnum postings = termsEnum.postings(null);
        for (int i = 0; i < docFreq; i++) {
            int docID = postings.nextDoc(); // 倒排表中下一个文档
            int freq = postings.freq(); // 词频: term在当前文档出现了几次
            for (int j = 0; j < freq; j++) {
                int position = postings.nextPosition(); // position：第j次出现的位置
                int startOffset = postings.startOffset();
                int endOffset = postings.endOffset();
                BytesRef payload = postings.getPayload();
            }
        }
        System.out.println("结束");
    }
}
