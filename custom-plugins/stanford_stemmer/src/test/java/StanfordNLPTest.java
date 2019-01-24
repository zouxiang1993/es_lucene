import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.junit.Test;

import java.util.Properties;

/**
 * <pre>
 *
 *  File: StanfordNLPTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/1/24				    zouxiang				Initial.
 *
 * </pre>
 */
public class StanfordNLPTest {
    // https://stanfordnlp.github.io/CoreNLP/api.html#quickstart-with-convenience-wrappers
    // Stanford CoreNLP 词干提取
    @Test
    public void test1() {
        Properties props = new Properties();
        // 要用到的是lemma, 它依赖 tokenize, ssplit, pos
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");

        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        CoreDocument document = new CoreDocument("drove droves");
        pipeline.annotate(document);

        String lemma0 = document.tokens().get(0).lemma();
        String lemma1 = document.tokens().get(1).lemma();
        System.out.println(lemma0 + "\t" + lemma1);  // 结果: drive	drove

    }
}


