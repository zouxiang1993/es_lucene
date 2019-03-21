package edu.stanford.nlp;

import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import org.junit.Test;

import java.util.List;

/**
 * <pre>
 *
 *  File: PosTest.java
 *
 *  Copyright (c) 2019, globalegrow.com All Rights Reserved.
 *
 *  Description:
 *  TODO
 *
 *  Revision History
 *  Date,					Who,					What;
 *  2019/3/14				    zouxiang				Initial.
 *
 * </pre>
 */
public class PosTest {
    @Test
    public void test1() {
//        Sentence sentence = new Sentence("front knotted bikini");
        Sentence sentence = new Sentence("shoe dress");
        List<Token> tokens = sentence.tokens();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.print(tokens.get(i).word() + "/" + sentence.posTag(i) + "\t");
            System.out.println();
        }
    }
}
