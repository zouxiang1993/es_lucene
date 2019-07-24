package edu.stanford.nlp.math;


import edu.stanford.nlp.util.RuntimeInterruptedException;
import edu.stanford.nlp.util.StringUtils;

import java.text.NumberFormat;
import java.util.Collection;

/**
 * Methods for operating on numerical arrays as vectors and matrices.
 *
 * @author Teg Grenager
 */
public class ArrayMath {
  public static double logSum(double... logInputs) {
    return logSum(logInputs,0,logInputs.length);
  }

  public static double logSum(double[] logInputs, int fromIndex, int toIndex) {
    if (Thread.interrupted()) {  // A good place to check for interrupts -- many functions call this
      throw new RuntimeInterruptedException();
    }
    if (logInputs.length == 0)
      throw new IllegalArgumentException();
    if(fromIndex >= 0 && toIndex < logInputs.length && fromIndex >= toIndex)
      return Double.NEGATIVE_INFINITY;
    int maxIdx = fromIndex;
    double max = logInputs[fromIndex];
    for (int i = fromIndex+1; i < toIndex; i++) {
      if (logInputs[i] > max) {
        maxIdx = i;
        max = logInputs[i];
      }
    }
    boolean haveTerms = false;
    double intermediate = 0.0;
    double cutoff = max - SloppyMath.LOGTOLERANCE;
    // we avoid rearranging the array and so test indices each time!
    for (int i = fromIndex; i < toIndex; i++) {
      if (i != maxIdx && logInputs[i] > cutoff) {
        haveTerms = true;
        intermediate += Math.exp(logInputs[i] - max);
      }
    }
    if (haveTerms) {
      return max + Math.log(1.0 + intermediate);
    } else {
      return max;
    }
  }

  public static double[] unbox(Collection<Double> list) {
    double[] result = new double[list.size()];
    int i = 0;
    for (double v : list) {
      result[i++] = v;
    }
    return result;
  }

  public static String toString(double[][] counts, int cellSize, Object[] rowLabels, Object[] colLabels, NumberFormat nf, boolean printTotals) {
    if (counts==null) return null;
    // first compute row totals and column totals
    double[] rowTotals = new double[counts.length];
    double[] colTotals = new double[counts[0].length]; // assume it's square
    double total = 0.0;
    for (int i = 0; i < counts.length; i++) {
      for (int j = 0; j < counts[i].length; j++) {
        rowTotals[i] += counts[i][j];
        colTotals[j] += counts[i][j];
        total += counts[i][j];
      }
    }
    StringBuilder result = new StringBuilder();
    // column labels
    if (colLabels != null) {
      result.append(StringUtils.padLeft("", cellSize));
      for (int j = 0; j < counts[0].length; j++) {
        String s = colLabels[j].toString();
        if (s.length() > cellSize - 1) {
          s = s.substring(0, cellSize - 1);
        }
        s = StringUtils.padLeft(s, cellSize);
        result.append(s);
      }
      if (printTotals) {
        result.append(StringUtils.padLeftOrTrim("Total", cellSize));
      }
      result.append('\n');
    }
    for (int i = 0; i < counts.length; i++) {
      // row label
      if (rowLabels != null) {
        String s = rowLabels[i].toString();
        s = StringUtils.padOrTrim(s, cellSize); // left align this guy only
        result.append(s);
      }
      // value
      for (int j = 0; j < counts[i].length; j++) {
        result.append(StringUtils.padLeft(nf.format(counts[i][j]), cellSize));
      }
      // the row total
      if (printTotals) {
        result.append(StringUtils.padLeft(nf.format(rowTotals[i]), cellSize));
      }
      result.append('\n');
    }
    // the col totals
    if (printTotals) {
      result.append(StringUtils.pad("Total", cellSize));
      for (double colTotal : colTotals) {
        result.append(StringUtils.padLeft(nf.format(colTotal), cellSize));
      }
      result.append(StringUtils.padLeft(nf.format(total), cellSize));
    }
    return result.toString();
  }

  /**
   * Makes the values in this array sum to 1.0. Does it in place.
   * If the total is 0.0, throws a RuntimeException.
   * If the total is Double.NEGATIVE_INFINITY, then it replaces the
   * array with a normalized uniform distribution. CDM: This last bit is
   * weird!  Do we really want that?
   */
  public static void logNormalize(double[] a) {
    double logTotal = logSum(a);
    if (logTotal == Double.NEGATIVE_INFINITY) {
      // to avoid NaN values
      double v = -Math.log(a.length);
      for (int i = 0; i < a.length; i++) {
        a[i] = v;
      }
      return;
    }
    addInPlace(a, -logTotal); // subtract log total from each value
  }

  /**
   * Increases the values in the first array a by b. Does it in place.
   *
   * @param a The array
   * @param b The amount by which to increase each item
   */
  public static void addInPlace(double[] a, double b) {
    for (int i = 0; i < a.length; i++) {
      a[i] = a[i] + b;
    }
  }

  /**
   * Add the two 1d arrays in place of {@code to}.
   *
   * @throws IllegalArgumentException If {@code to} and {@code from} are not of the same dimensions
   */
  public static void pairwiseAddInPlace(double[] to, double[] from) {
    if (to.length != from.length) {
      throw new IllegalArgumentException("to length:" + to.length + " from length:" + from.length);
    }
    for (int i = 0; i < to.length; i++) {
      to[i] = to[i] + from[i];
    }
  }
}
