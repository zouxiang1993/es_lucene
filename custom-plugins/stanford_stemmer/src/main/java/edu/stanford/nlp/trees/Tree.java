package edu.stanford.nlp.trees;

import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.LabelFactory;
import edu.stanford.nlp.util.Generics;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * The abstract class {@code Tree} is used to collect all of the
 * tree types, and acts as a generic extensible type.  This is the
 * standard implementation of inheritance-based polymorphism.
 * All {@code Tree} objects support accessors for their children (a
 * {@code Tree[]}), their label (a {@code Label}), and their
 * score (a {@code double}).  However, different concrete
 * implementations may or may not include the latter two, in which
 * case a default value is returned.  The class Tree defines no data
 * fields.  The two abstract methods that must be implemented are:
 * {@code children()}, and {@code treeFactory()}.  Notes
 * that {@code setChildren(Tree[])} is now an optional
 * operation, whereas it was previously required to be
 * implemented. There is now support for finding the parent of a
 * tree.  This may be done by search from a tree root, or via a
 * directly stored parent.  The {@code Tree} class now
 * implements the {@code Collection} interface: in terms of
 * this, each <i>node</i> of the tree is an element of the
 * collection; hence one can explore the tree by using the methods of
 * this interface.  A {@code Tree} is regarded as a read-only
 * {@code Collection} (even though the {@code Tree} class
 * has various methods that modify trees).  Moreover, the
 * implementation is <i>not</i> thread-safe: no attempt is made to
 * detect and report concurrent modifications.
 *
 * @author Christopher Manning
 * @author Dan Klein
 * @author Sarah Spikes (sdspikes@cs.stanford.edu) - filled in types
 */
public abstract class Tree extends AbstractCollection<Tree> implements Label, Labeled, Scored, Serializable  {

  private static final long serialVersionUID = 5441849457648722744L;

  /**
   * A leaf node should have a zero-length array for its
   * children. For efficiency, classes can use this array as a
   * return value for children() for leaf nodes if desired.
   * This can also be used elsewhere when you want an empty Tree array.
   */
  public static final Tree[] EMPTY_TREE_ARRAY = new Tree[0];

  public Tree() {
  }

  /**
   * Says whether a node is a leaf.  Can be used on an arbitrary
   * {@code Tree}.  Being a leaf is defined as having no
   * children.  This must be implemented as returning a zero-length
   * Tree[] array for children().
   *
   * @return true if this object is a leaf
   */
  public boolean isLeaf() {
    return numChildren() == 0;
  }


  /**
   * Says how many children a tree node has in its local tree.
   * Can be used on an arbitrary {@code Tree}.  Being a leaf is defined
   * as having no children.
   *
   * @return The number of direct children of the tree node
   */
  public int numChildren() {
    return children().length;
  }

  /**
   * Return whether this node is a preterminal or not.  A preterminal is
   * defined to be a node with one child which is itself a leaf.
   *
   * @return true if the node is a preterminal; false otherwise
   */
  public boolean isPreTerminal() {
    Tree[] kids = children();
    return (kids.length == 1) && (kids[0].isLeaf());
  }

  /**
   * Implements equality for Tree's.  Two Tree objects are equal if they
   * have equal {@link #value}s, the same number of children, and their children
   * are pairwise equal.
   *
   * @param o The object to compare with
   * @return Whether two things are equal
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Tree)) {
      return false;
    }
    Tree t = (Tree) o;
    String value1 = this.value();
    String value2 = t.value();
    if (value1 != null || value2 != null) {
    	if (value1 == null || value2 == null || !value1.equals(value2)) {
    		return false;
    	}
    }
    Tree[] myKids = children();
    Tree[] theirKids = t.children();
    //if((myKids == null && (theirKids == null || theirKids.length != 0)) || (theirKids == null && myKids.length != 0) || (myKids.length != theirKids.length)){
    if (myKids.length != theirKids.length) {
      return false;
    }
    for (int i = 0; i < myKids.length; i++) {
      if (!myKids[i].equals(theirKids[i])) {
        return false;
      }
    }
    return true;
  }


  /**
   * Implements a hashCode for Tree's.  Two trees should have the same
   * hashcode if they are equal, so we hash on the label value and
   * the children's label values.
   *
   * @return The hash code
   */
  @Override
  public int hashCode() {
    String v = this.value();
    int hc = (v == null) ? 1 : v.hashCode();
    Tree[] kids = children();
    for (int i = 0; i < kids.length; i++) {
      v = kids[i].value();
      int hc2 = (v == null) ? i : v.hashCode();
      hc ^= (hc2 << i);
    }
    return hc;
  }

  /**
   * Returns an array of children for the current node.  If there
   * are no children (if the node is a leaf), this must return a
   * Tree[] array of length 0.  A null children() value for tree
   * leaves was previously supported, but no longer is.
   * A caller may assume that either {@code isLeaf()} returns
   * true, or this node has a nonzero number of children.
   *
   * @return The children of the node
   * @see #getChildrenAsList()
   */
  public abstract Tree[] children();


  /**
   * Returns a List of children for the current node.  If there are no
   * children, then a (non-null) {@code List<Tree>} of size 0 will
   * be returned.  The list has new list structure but pointers to,
   * not copies of the children.  That is, the returned list is mutable,
   * and simply adding to or deleting items from it is safe, but beware
   * changing the contents of the children.
   *
   * @return The children of the node
   */
  public List<Tree> getChildrenAsList() {
    return new ArrayList<>(Arrays.asList(children()));
  }

  /**
   * Returns the label associated with the current node, or null
   * if there is no label.  The default implementation always
   * returns {@code null}.
   *
   * @return The label of the node
   */
  @Override
  public Label label() {
    return null;
  }


  /**
   * Sets the label associated with the current node, if there is one.
   * The default implementation ignores the label.
   *
   * @param label The label
   */
  @Override
  public void setLabel(Label label) {
    // a noop
  }


  /**
   * Returns the score associated with the current node, or NaN
   * if there is no score.  The default implementation returns NaN.
   *
   * @return The score
   */
  @Override
  public double score() {
    return Double.NaN;
  }

  /**
   * Most instances of {@code Tree} will take a lot more than
   * than the default {@code StringBuffer} size of 16 to print
   * as an indented list of the whole tree, so we enlarge the default.
   */
  private static final int initialPrintStringBuilderSize = 500;

  /**
   * Appends the printed form of a parse tree (as a bracketed String)
   * to a {@code StringBuilder}.
   * The implementation of this may be more efficient than for
   * {@code toString()} on complex trees.
   *
   * @param sb The {@code StringBuilder} to which the tree will be appended
   * @return Returns the {@code StringBuilder} passed in with extra stuff in it
   */
  public StringBuilder toStringBuilder(StringBuilder sb) {
    return toStringBuilder(sb, label -> (label.value() == null) ? "": label.value());
  }

  /**
   * Appends the printed form of a parse tree (as a bracketed String)
   * to a {@code StringBuilder}.
   * The implementation of this may be more efficient than for
   * {@code toString()} on complex trees.
   *
   * @param sb The {@code StringBuilder} to which the tree will be appended
   * @param labelFormatter Formatting routine for how to print a Label
   * @return Returns the {@code StringBuilder} passed in with extra stuff in it
   */
  public StringBuilder toStringBuilder(StringBuilder sb, Function<Label,String> labelFormatter) {
    if (isLeaf()) {
      if (label() != null) {
        sb.append(labelFormatter.apply(label()));
      }
      return sb;
    } else {
      sb.append('(');
      if (label() != null) {
        sb.append(labelFormatter.apply(label()));
      }
      Tree[] kids = children();
      if (kids != null) {
        for (Tree kid : kids) {
          sb.append(' ');
          kid.toStringBuilder(sb, labelFormatter);
        }
      }
      return sb.append(')');
    }
  }


  /**
   * Converts parse tree to string in Penn Treebank format.
   *
   * Implementation note: Internally, the method gains
   * efficiency by chaining use of a single {@code StringBuilder}
   * through all the printing.
   *
   * @return the tree as a bracketed list on one line
   */
  @Override
  public String toString() {
    return toStringBuilder(new StringBuilder(Tree.initialPrintStringBuilderSize)).toString();
  }

  private static void displayChildren(Tree[] trChildren, int indent, boolean parentLabelNull,
                                      Function<Label,String> labelFormatter, PrintWriter pw) {
    boolean firstSibling = true;
    boolean leftSibIsPreTerm = true;  // counts as true at beginning
    for (Tree currentTree : trChildren) {
      currentTree.display(indent, parentLabelNull, firstSibling, leftSibIsPreTerm, false, labelFormatter, pw);
      leftSibIsPreTerm = currentTree.isPreTerminal();
      // CC is a special case for English, but leave it in so we can exactly match PTB3 tree formatting
      if (currentTree.value() != null && currentTree.value().startsWith("CC")) {
        leftSibIsPreTerm = false;
      }
      firstSibling = false;
    }
  }

  /**
   * Display a node, implementing Penn Treebank style layout
   */
  private void display(int indent, boolean parentLabelNull, boolean firstSibling, boolean leftSiblingPreTerminal, boolean topLevel, Function<Label,String> labelFormatter, PrintWriter pw) {
    // the condition for staying on the same line in Penn Treebank
    boolean suppressIndent = (parentLabelNull || (firstSibling && isPreTerminal()) || (leftSiblingPreTerminal && isPreTerminal() && (label() == null || !label().value().startsWith("CC"))));
    if (suppressIndent) {
      pw.print(" ");
      // pw.flush();
    } else {
      if (!topLevel) {
        pw.println();
      }
      for (int i = 0; i < indent; i++) {
        pw.print("  ");
        // pw.flush();
      }
    }
    if (isLeaf() || isPreTerminal()) {
      String terminalString = toStringBuilder(new StringBuilder(), labelFormatter).toString();
      pw.print(terminalString);
      pw.flush();
      return;
    }
    pw.print("(");
    pw.print(labelFormatter.apply(label()));
    // pw.flush();
    boolean parentIsNull = label() == null || label().value() == null;
    displayChildren(children(), indent + 1, parentIsNull, labelFormatter, pw);
    pw.print(")");
    pw.flush();
  }

  /**
   * Get the set of all node and leaf {@code Label}s,
   * null or otherwise, contained in the tree.
   *
   * @return the {@code Collection} (actually, Set) of all values
   *         in the tree.
   */
  @Override
  public Collection<Label> labels() {
    Set<Label> n = Generics.newHashSet();
    n.add(label());
    Tree[] kids = children();
    for (Tree kid : kids) {
      n.addAll(kid.labels());
    }
    return n;
  }


  @Override
  public void setLabels(Collection<Label> c) {
    throw new UnsupportedOperationException("Can't set Tree labels");
  }


  /**
   * Return the parent of the tree node.  This routine will traverse
   * a tree (depth first) from the given {@code root}, and will
   * correctly find the parent, regardless of whether the concrete
   * class stores parents.  It will only return {@code null} if this
   * node is the {@code root} node, or if this node is not
   * contained within the tree rooted at {@code root}.
   *
   * @param root The root node of the whole Tree
   * @return the parent {@code Tree} node if any;
   *         else {@code null}
   */
  public Tree parent(Tree root) {
    Tree[] kids = root.children();
    return parentHelper(root, kids, this);
  }

  private static Tree parentHelper(Tree parent, Tree[] kids, Tree node) {
    for (Tree kid : kids) {
      if (kid == node) {
        return parent;
      }
      Tree ret = node.parent(kid);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }

  @Override
  public int size() {
    int size = 1;
    Tree[] kids = children();
    for (Tree kid : kids) {
      size += kid.size();
    }
    return size;
  }

  private static class TreeIterator implements Iterator<Tree> {

    private final List<Tree> treeStack;

    protected TreeIterator(Tree t) {
      treeStack = new ArrayList<>();
      treeStack.add(t);
    }

    @Override
    public boolean hasNext() {
      return (!treeStack.isEmpty());
    }

    @Override
    public Tree next() {
      int lastIndex = treeStack.size() - 1;
      if (lastIndex < 0) {
        throw new NoSuchElementException("TreeIterator exhausted");
      }
      Tree tr = treeStack.remove(lastIndex);
      Tree[] kids = tr.children();
      // so that we can efficiently use one List, we reverse them
      for (int i = kids.length - 1; i >= 0; i--) {
        treeStack.add(kids[i]);
      }
      return tr;
    }

    /**
     * Not supported
     */
    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      return "TreeIterator";
    }

  }


  /**
   * Returns an iterator over all the nodes of the tree.  This method
   * implements the {@code iterator()} method required by the
   * {@code Collections} interface.  It does a preorder
   * (children after node) traversal of the tree.  (A possible
   * extension to the class at some point would be to allow different
   * traversal orderings via variant iterators.)
   *
   * @return An iterator over the nodes of the tree
   */
  @Override
  public Iterator<Tree> iterator() {
    return new TreeIterator(this);
  }

  /**
   * This gives you a tree from a String representation (as a
   * bracketed Tree, of the kind produced by {@code toString()},
   * {@code pennPrint()}, or as in the Penn Treebank.
   * It's not the most efficient thing to do for heavy duty usage.
   *
   * @param str The tree as a bracketed list in a String.
   * @param trf The TreeFactory used to make the new Tree
   * @return The Tree
   * @throws RuntimeException If the Tree format is not valid
   */
  public static Tree valueOf(String str, TreeReaderFactory trf) {
    try {
      return trf.newTreeReader(new StringReader(str)).readTree();
    } catch (IOException ioe) {
      throw new RuntimeException("Tree.valueOf() tree construction failed", ioe);
    }
  }

  private Tree[] dominationPathHelper(Tree t, int depth) {
    Tree[] kids = children();
    for (int i = kids.length - 1; i >= 0; i--) {
      Tree t1 = kids[i];
      if (t1 == null) {
        return null;
      }
      Tree[] result;
      if ((result = t1.dominationPath(t, depth + 1)) != null) {
        result[depth] = this;
        return result;
      }
    }
    return null;
  }

  private Tree[] dominationPath(Tree t, int depth) {
    if (this == t) {
      Tree[] result = new Tree[depth + 1];
      result[depth] = this;
      return result;
    }
    return dominationPathHelper(t, depth);
  }

  @Override
  public String value() {
    Label lab = label();
    if (lab == null) {
      return null;
    }
    return lab.value();
  }

  @Override
  public void setValue(String value) {
    Label lab = label();
    if (lab != null) {
      lab.setValue(value);
    }
  }

  @Override
  public void setFromString(String labelStr) {
    Label lab = label();
    if (lab != null) {
      lab.setFromString(labelStr);
    }
  }

  /**
   * Returns a factory that makes labels of the same type as this one.
   * May return {@code null} if no appropriate factory is known.
   *
   * @return the LabelFactory for this kind of label
   */
  @Override
  public LabelFactory labelFactory() {
    Label lab = label();
    if (lab == null) {
      return null;
    }
    return lab.labelFactory();
  }

}
