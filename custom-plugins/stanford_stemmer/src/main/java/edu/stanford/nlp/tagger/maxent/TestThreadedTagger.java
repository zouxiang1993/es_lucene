// TestThreadedTagger -- StanfordMaxEnt, A Maximum Entropy Toolkit
// Copyright (c) 2002-2011 Leland Stanford Junior University
//
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
// For more information, bug reports, fixes, contact:
//    Christopher Manning
//    Dept of Computer Science, Gates 1A
//    Stanford CA 94305-9010
//    USA
//    Support/Questions: java-nlp-user@lists.stanford.edu
//    Licensing: java-nlp-support@lists.stanford.edu
//    http://www-nlp.stanford.edu/software/tagger.shtml
package edu.stanford.nlp.tagger.maxent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import edu.stanford.nlp.util.StringUtils;

/**
 * First, this runs a tagger once to see what results it comes up with.
 * Then it runs the same tagger in two separate threads to make sure the results are the same.
 * The results are printed to stdout; the user is expected to verify they are as expected.
 *
 * Normally you would run MaxentTagger with command line arguments such as:
 *
 * -model ../data/tagger/my-left3words-distsim-wsj-0-18.tagger
 * -testFile ../data/tagger/test-wsj-19-21 -verboseResults false
 *
 * If you provide the same arguments to this program, it will first
 * run the given tagger on the given test file once to establish the
 * "baseline" results.  It will then run the same tagger in more than
 * one thread at the same time; the output for both threads should be
 * the same if the MaxentTagger is re-entrant.  The number of threads
 * to be run can be specified with -numThreads; the default is
 * DEFAULT_NUM_THREADS.
 *
 * You can also provide multiple models.  After performing that test
 * on model1, it will then run the same test file on model2, model3,
 * etc to establish baseline results for that tagger.  After that, it
 * runs both taggers at the same time.  The taggers should be
 * completely separate structures.  In other words, the second tagger
 * should not have clobbered any static state initialized by the first
 * tagger.  Thus, the results of the two simultaneous taggers should
 * be the same as the two taggers' baselines.
 *
 * Example arguments for the more complicated test:
 *
 * -model1 ../data/pos-tagger/newmodels/left3words-distsim-wsj-0-18.tagger
 * -model2 ../data/pos-tagger/newmodels/left3words-wsj-0-18.tagger
 * -testFile ../data/pos-tagger/training/english/test-wsj-19-21
 * -verboseResults false
 *
 * @author John Bauer
 */
class TestThreadedTagger {
  /**
   * Default number of threads to launch in the first test.
   * Can be specified with -numThreads.
   */
  static final int DEFAULT_NUM_THREADS = 2;

  static final String THREAD_FLAG = "numThreads";


  private TestThreadedTagger() {} // static methods



}
