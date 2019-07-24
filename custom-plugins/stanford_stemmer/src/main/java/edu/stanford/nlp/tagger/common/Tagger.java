package edu.stanford.nlp.tagger.common;

/**
 * This module includes constants that are the same for all taggers,
 * as opposed to being part of their configurations.
 * Also, can be used as an interface if you don't want to necessarily
 * include the MaxentTagger code, such as in public releases which
 * don't include that code.
 *
 * @author John Bauer
 */
public abstract class Tagger{

  public static final String EOS_TAG = ".$$.";
  public static final String EOS_WORD = ".$.";

}
