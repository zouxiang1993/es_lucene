package lucene.util.fst;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.*;
import org.junit.Test;

import java.io.IOException;

public class BuilderTest {
    /**
     * FST的构造过程
     *
     * @throws IOException
     */
    @Test
    public void testBuildFST() throws IOException {
        FST<Long> fst = buildFST();
        System.out.println(fst);
    }

    /**
     * FST的查询过程
     *
     * @throws IOException
     */
    @Test
    public void testQueryByFST() throws IOException {
        FST<Long> fst = buildFST();
        BytesRef bytesRef = new BytesRef("dog");
        IntsRef intsRef = Util.toIntsRef(bytesRef, new IntsRefBuilder());
        Long result = Util.get(fst, intsRef);
        System.out.println(result);
    }

    /**
     * 遍历FST
     * @throws IOException
     */
    @Test
    public void testFST_Iterator() throws IOException {
        FST<Long> fst = buildFST();
        IntsRefFSTEnum<Long> fstEnum = new IntsRefFSTEnum<>(fst);
        IntsRefFSTEnum.InputOutput<Long> inputOutput;
        BytesRefBuilder scratch = new BytesRefBuilder();
        while ((inputOutput = fstEnum.next()) != null) {
            String input = Util.toBytesRef(inputOutput.input, scratch).utf8ToString();
            Long output = inputOutput.output;
            System.out.println(input +"\t" + output);
        }
    }

    private FST<Long> buildFST() throws IOException {
        String inputValues[] = {"cat", "deep", "do", "dog", "dogs"};
        long outputValues[] = {5, 10, 15, 2, 8};

        PositiveIntOutputs outputs = PositiveIntOutputs.getSingleton();
        Builder<Long> builder = new Builder<Long>(FST.INPUT_TYPE.BYTE1, outputs);
        IntsRefBuilder scratchInts = new IntsRefBuilder();
        for (int i = 0; i < inputValues.length; i++) {
            BytesRef scratchBytes = new BytesRef(inputValues[i]);
            builder.add(Util.toIntsRef(scratchBytes, scratchInts), outputValues[i]);
        }
        FST<Long> fst = builder.finish();
        return fst;
    }
}
