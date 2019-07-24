package edu.stanford.nlp.util;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * A collection of utilities to make dealing with Java generics less
 * painful and verbose.  For example, rather than declaring
 *
 * <pre>
 * {@code  Map<String,ClassicCounter<List<String>>> = new HashMap<String,ClassicCounter<List<String>>>()}
 * </pre>
 * <p>
 * you just call <code>Generics.newHashMap()</code>:
 *
 * <pre>
 * {@code Map<String,ClassicCounter<List<String>>> = Generics.newHashMap()}
 * </pre>
 * <p>
 * Java type-inference will almost always just <em>do the right thing</em>
 * (every once in a while, the compiler will get confused before you do,
 * so you might still occasionally have to specify the appropriate types).
 * <p>
 * This class is based on the examples in Brian Goetz's article
 * <a href="http://www.ibm.com/developerworks/library/j-jtp02216.html">Java
 * theory and practice: The pseudo-typedef antipattern</a>.
 *
 * @author Ilya Sherman
 */
public class Generics {

    private Generics() {
    } // static class

    public static final String HASH_MAP_PROPERTY = "edu.stanford.nlp.hashmap.impl";
    public static final String HASH_MAP_CLASSNAME = System.getProperty(HASH_MAP_PROPERTY);
    public static final String HASH_SET_PROPERTY = "edu.stanford.nlp.hashset.impl";
    public static final String HASH_SET_CLASSNAME = System.getProperty(HASH_SET_PROPERTY);
    private static final Class<?> HASH_SET_CLASS = getHashSetClass();
    private static final Class<?> HASH_MAP_CLASS = getHashMapClass();
    private static final Constructor HASH_SET_SIZE_CONSTRUCTOR = getHashSetSizeConstructor();
    private static final Constructor HASH_MAP_SIZE_CONSTRUCTOR = getHashMapSizeConstructor();
    private static final Constructor HASH_SET_COLLECTION_CONSTRUCTOR = getHashSetCollectionConstructor();

    // must be called after HASH_MAP_CLASS is defined
    private static Constructor getHashMapSizeConstructor() {
        try {
            return HASH_MAP_CLASS.getConstructor(Integer.TYPE);
        } catch (Exception e) {
//            log.info("Warning: could not find a constructor for objects of " + HASH_MAP_CLASS + " which takes an integer argument.  Will use the no argument constructor instead.");
        }
        return null;
    }
    // must be called after HASH_SET_CLASS is defined
    private static Constructor getHashSetCollectionConstructor() {
        try {
            return HASH_SET_CLASS.getConstructor(Collection.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: could not find a constructor for objects of " + HASH_SET_CLASS + " which takes an existing collection argument.", e);
        }
    }

    private static Class getHashMapClass() {
        try {
            if (HASH_MAP_CLASSNAME == null) {
                return HashMap.class;
            } else {
                return Class.forName(HASH_MAP_CLASSNAME);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class getHashSetClass() {
        try {
            if (HASH_SET_CLASSNAME == null) {
                return HashSet.class;
            } else {
                return Class.forName(HASH_SET_CLASSNAME);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> Set<E> newHashSet() {
        try {
            return ErasureUtils.uncheckedCast(HASH_SET_CLASS.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> Set<E> newHashSet(int initialCapacity) {
        if (HASH_SET_SIZE_CONSTRUCTOR == null) {
            return newHashSet();
        }
        try {
            return ErasureUtils.uncheckedCast(HASH_SET_SIZE_CONSTRUCTOR.newInstance(initialCapacity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // must be called after HASH_SET_CLASS is defined
    private static Constructor getHashSetSizeConstructor() {
        try {
            return HASH_SET_CLASS.getConstructor(Integer.TYPE);
        } catch (Exception e) {
            // TODO: log
//      log.info("Warning: could not find a constructor for objects of " + HASH_SET_CLASS + " which takes an integer argument.  Will use the no argument constructor instead.");
        }
        return null;
    }

    /* Maps */
    public static <K,V> Map<K,V> newHashMap() {
        try {
            return ErasureUtils.uncheckedCast(HASH_MAP_CLASS.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <K,V> Map<K,V> newHashMap(int initialCapacity) {
        if (HASH_MAP_SIZE_CONSTRUCTOR == null) {
            return newHashMap();
        }
        try {
            return ErasureUtils.uncheckedCast(HASH_MAP_SIZE_CONSTRUCTOR.newInstance(initialCapacity));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <E> Set<E> newHashSet(Collection<? extends E> c) {
        try {
            return ErasureUtils.uncheckedCast(HASH_SET_COLLECTION_CONSTRUCTOR.newInstance(c));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Collections */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }
}
