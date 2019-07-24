package edu.stanford.nlp.util;

import java.util.*;

/**
 * Collection of useful static methods for working with Collections. Includes
 * methods to increment counts in maps and cast list/map elements to common
 * types.
 *
 * @author Joseph Smarr (jsmarr@stanford.edu)
 */
public class CollectionUtils  {
  /** Returns a new List containing the given objects. */
  @SafeVarargs
  public static <T> List<T> makeList(T... items) {
    return new ArrayList<>(Arrays.asList(items));
  }

  public static <T extends Comparable<T>> List<T> sorted(Iterable<T> items) {
    List<T> result = toList(items);
    Collections.sort(result);
    return result;
  }

  public static <T> List<T> toList(Iterable<T> items) {
    List<T> list = new ArrayList<>();
    addAll(list, items);
    return list;
  }

  public static <T> void addAll(Collection<T> collection, Iterable<? extends T> items) {
    for (T item : items) {
      collection.add(item);
    }
  }
}
