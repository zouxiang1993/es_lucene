package edu.stanford.nlp.util;

import java.util.List;
import java.util.Set;


public interface PriorityQueue<E> extends Set<E> {

  /**
   * Finds the object with the highest priority, removes it,
   * and returns it.
   *
   * @return the object with highest priority
   */
  public E removeFirst();

  /**
   * Finds the object with the highest priority and returns it, without
   * modifying the queue.
   *
   * @return the object with minimum key
   */
  public E getFirst();

  /**
   * Gets the priority of the highest-priority element of the queue
   * (without modifying the queue).
   *
   * @return The priority of the highest-priority element of the queue.
   */
  public double getPriority();

  /**
   * Get the priority of a key.
   *
   * @param key The object to assess
   * @return A key's priority. If the key is not in the queue,
   *         Double.NEGATIVE_INFINITY is returned.
   */
  public double getPriority(E key);

  /**
   * Convenience method for if you want to pretend relaxPriority doesn't exist,
   * or if you really want to use the return conditions of add().
   * <p>
   * Warning: The semantics of this method currently varies between implementations.
   * In some implementations, nothing will be changed if the key is already in the
   * priority queue. In others, the element will be added a second time with the
   * new priority. We maybe should at least change things so that the priority
   * will be change to the priority given if the element is in the queue with
   * a lower priority, but that wasn't the historical behavior, and it seemed like
   * we'd need to do a lot of archeology before changing the behavior.
   *
   * @return {@code true} if this set did not already contain the specified
   *         element.
   */
  public boolean add(E key, double priority);


  /**
   * Changes a priority, either up or down, adding the key it if it wasn't there already.
   *
   * @param key an {@code E} value
   * @return whether the priority actually changed.
   */
  public boolean changePriority(E key, double priority);

  /**
   * Increases the priority of the E key to the new priority if the old priority
   * was lower than the new priority. Otherwise, does nothing.
   *
   */
  public boolean relaxPriority(E key, double priority);

  public List<E> toSortedList();

  /**
   * Returns a representation of the queue in decreasing priority order,
   * displaying at most maxKeysToPrint elements.
   *
   * @param maxKeysToPrint The maximum number of keys to print. Less are
   *     printed if there are less than this number of items in the
   *     PriorityQueue. If this number is non-positive, then all elements in
   *     the PriorityQueue are printed.
   * @return A String representation of the high priority items in the queue.
   */
  public String toString(int maxKeysToPrint);

}
