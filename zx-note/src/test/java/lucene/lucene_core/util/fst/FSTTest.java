package lucene.lucene_core.util.fst;

import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.util.fst.*;
import org.junit.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;

public class FSTTest {
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
     *
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
            System.out.println(input + "\t" + output);
        }
    }

    /**
     * 将FST绘制成图<br/>
     * 将输出的文本复制到 Graphviz2.38\bin\gvedit.exe 可以绘图。
     *
     * @throws IOException
     */
    @Test
    public void testDrawFST() throws IOException {
        FST<Long> fst = buildFST();
        Util.toDot(fst, new PrintWriter(System.out), false, true);
    }

    @Test
    public void testShortestPath() throws IOException {
        final String userInput = "do"; // 假如用户输入了"do"
        BytesRef bytesRef = new BytesRef(userInput);
        IntsRef input = Util.toIntsRef(bytesRef, new IntsRefBuilder());

        FST<Long> fst = buildFST();

        // 从开始位置走到"do"位置
        FST.Arc<Long> arc = fst.getFirstArc(new FST.Arc<>());
        FST.BytesReader fstReader = fst.getBytesReader();
        for (int i = 0; i < input.length; i++) {
            if (fst.findTargetArc(input.ints[input.offset + i], arc, arc, fstReader) == null) {
                System.out.println("没找到。。。");
                return;
            }
        }

        // 从"do"位置开始找走到终止状态最近的2条路径
        Util.TopResults<Long> results = Util.shortestPaths(fst, arc, PositiveIntOutputs.getSingleton().getNoOutput(), new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }
        }, 2, false);

        // 打印结果： dog  dogs。 即用户输入"do"，给用户建议"dog"和"dogs"
        BytesRefBuilder bytesRefBuilder = new BytesRefBuilder();
        for (Util.Result<Long> result : results) {
            IntsRef intsRef = result.input;
            System.out.println(userInput + Util.toBytesRef(intsRef, bytesRefBuilder).utf8ToString());
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
