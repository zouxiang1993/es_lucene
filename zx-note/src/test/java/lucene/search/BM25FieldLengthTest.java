package lucene.search;

import org.apache.lucene.util.SmallFloat;
import org.junit.Test;

/**
 * <pre>
 *
 *  File: BM25FieldLengthTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/1/15				    zouxiang				Initial.
 *
 * </pre>
 */
public class BM25FieldLengthTest {
    private static final float[] NORM_TABLE = new float[256];  // NORM_TABLE 值随下标递减

    static {
        for (int i = 1; i < 256; i++) {
            float f = SmallFloat.byte315ToFloat((byte) i);
            NORM_TABLE[i] = 1.0f / (f * f);
        }
        NORM_TABLE[0] = 1.0f / NORM_TABLE[255];
    }

    private float decodeNormValue(byte b) {
        return NORM_TABLE[b & 0xFF];
    }

    protected byte encodeNormValue(float boost, float length) {
        return SmallFloat.floatToByte315((boost / (float) Math.sqrt(length)));
    }

    /**
     * 查看NORM_TABLE[] 的值
     * NORM_TABLE[]可以理解为Norm值到字段长度的映射
     * NORM_TABLE 值随下标递减
     */
    @Test
    public void testNormTable() {
        for (int i = 0; i < NORM_TABLE.length; i++) {
            if (i > 0 && NORM_TABLE[i] > NORM_TABLE[i - 1]) {
                System.out.println("ERROR!!!!!!!!!!");
                System.exit(-1);
            }
            System.out.printf("%s\t---%.30f\n", i, NORM_TABLE[i]);
        }
    }

    /**
     * BM25中，norm 是 字段长度归一值 与 索引时boost(如果存在) 结合后的值。<br>
     * 这个因子只与文档有关，与Query无关，因此是在index时写入到索引文件中。<br>
     * 但这个值是每个文档的每个字段都有的值，使用int或者float存储消耗空间太多。<br>
     * Lucene采用一个byte来存储。这个byte存储的就是NORM_TABLE的下标<br>
     *
     * 索引时使用encodeNormValue(float boost, int length)来计算norm下标<br>
     * 查询时使用decodeNormValue(byte)来根据下标查找norm值<br>
     * <p>
     * decodeNormValue(encodeNormValue(boost, length)) 随 length 递增
     */
    @Test
    public void testEncodeNormValueAndDecodeNormValue() {
        final float indexBoost = 1.0f;  // 索引时一般都不设置boost
        for (int fieldLength = 1; fieldLength <= 100; fieldLength++) {
            byte idx = encodeNormValue(indexBoost, fieldLength);
            System.out.printf("%s \t %s \t %.2f\n", fieldLength, idx, decodeNormValue(idx));
        }
    }
}
