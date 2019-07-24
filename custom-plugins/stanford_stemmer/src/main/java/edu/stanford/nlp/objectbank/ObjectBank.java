package edu.stanford.nlp.objectbank;

import edu.stanford.nlp.util.AbstractIterator;
import java.util.function.Function;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.Reader;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;


public class ObjectBank<E> implements Collection<E>, Serializable {

  private boolean keepInMemory; // = false;

  public ObjectBank(ReaderIteratorFactory rif, IteratorFromReaderFactory<E> ifrf) {
    this.rif = rif;
    this.ifrf = ifrf;
  }


  @SuppressWarnings({"NonSerializableFieldInSerializableClass"})
  protected ReaderIteratorFactory rif;

  @SuppressWarnings({"NonSerializableFieldInSerializableClass"})
  protected IteratorFromReaderFactory<E> ifrf;

  @SuppressWarnings({"NonSerializableFieldInSerializableClass"})
  private List<E> contents; // = null;


  public static ObjectBank<String> getLineIterator(File file) {
    return getLineIterator(Collections.singleton(file), new IdentityFunction<>());
  }


  public static <X> ObjectBank<X> getLineIterator(Collection<?> filesStringsAndReaders, Function<String,X> op) {
    ReaderIteratorFactory rif = new ReaderIteratorFactory(filesStringsAndReaders);
    IteratorFromReaderFactory<X> ifrf = LineIterator.getFactory(op);
    return new ObjectBank<>(rif, ifrf);
  }


  @Override
  public Iterator<E> iterator() {

    // basically concatenates Iterator's made from
    // each java.io.Reader.
    if (keepInMemory) {
      if (contents == null) {
        contents = new ArrayList<>();
        Iterator<E> iter = new OBIterator();
        while (iter.hasNext()) {
          contents.add(iter.next());
        }
      }
      return contents.iterator();
    }

    return new OBIterator();
  }

  @Override
  public boolean isEmpty() {
    return ! iterator().hasNext();
  }

  /**
   * Can be slow.  Usage not recommended.
   */
  @Override
  public boolean contains(Object o) {
    for (E e : this) {
      if (e == o) {
        return true;
      }
    }
    return false;
  }

  /**
   * Can be slow.  Usage not recommended.
   */
  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object obj : c) {
      if ( ! contains(obj)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Can be slow.  Usage not recommended.
   */
  @Override
  public int size() {
    Iterator<E> iter = iterator();
    int size = 0;
    while (iter.hasNext()) {
      size++;
      iter.next();
    }
    return size;
  }

  @Override
  public void clear() {
    rif = new ReaderIteratorFactory();
  }

  @Override
  public Object[] toArray() {
    Iterator<E> iter = iterator();
    ArrayList<Object> al = new ArrayList<>();
    while (iter.hasNext()) {
      al.add(iter.next());
    }
    return al.toArray();
  }

  /**
   * Can be slow.  Usage not recommended.
   */
  @Override
  @SuppressWarnings({"SuspiciousToArrayCall"})
  public <T> T[] toArray(T[] o) {
    Iterator<E> iter = iterator();
    ArrayList<E> al = new ArrayList<>();
    while (iter.hasNext()) {
      al.add(iter.next());
    }
    return al.toArray(o);
  }


  /**
   * Unsupported Operation.  If you wish to add a new data source,
   * do so in the underlying ReaderIteratorFactory
   */
  @Override
  public boolean add(E o) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported Operation.  If you wish to remove a data source,
   * do so in the underlying ReaderIteratorFactory
   */
  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported Operation.  If you wish to add new data sources,
   * do so in the underlying ReaderIteratorFactory
   */
  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported Operation.  If you wish to remove data sources,
   * remove, do so in the underlying ReaderIteratorFactory.
   */
  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Unsupported Operation.  If you wish to retain only certain data
   * sources, do so in the underlying ReaderIteratorFactory.
   */
  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  /**
   * Iterator of Objects.
   */
  class OBIterator extends AbstractIterator<E> {

    private final Iterator<Reader> readerIterator;
    private Iterator<E> tok;
    private E nextObject;
    private Reader currReader; // = null;

    public OBIterator() {
      readerIterator = rif.iterator();
      setNextObject();
    }

    private void setNextObject() {
      if (tok != null && tok.hasNext()) {
        nextObject = tok.next();
      } else {
        setNextObjectHelper();
      }
    }

    private void setNextObjectHelper() {
      while (true) {
        try {
          if (currReader != null) {
            currReader.close();
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        if (readerIterator.hasNext()) {
          currReader = readerIterator.next();
          tok = ifrf.getIterator(currReader);
        } else {
          nextObject = null;
          return;
        }

        if (tok.hasNext()) {
          nextObject = tok.next();
          return;
        }
      }
    }

    @Override
    public boolean hasNext() {
      return nextObject != null;
    }

    @Override
    public E next() {
      if (nextObject == null) {
        throw new NoSuchElementException();
      }
      E tmp = nextObject;
      setNextObject();
      return tmp;
    }

  } // end class OBIterator

  private static final long serialVersionUID = -4030295596701541770L;

}
