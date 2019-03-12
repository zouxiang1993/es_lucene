package lucene.lucene_core.analysis;

public class Pair<S, T> {
    private S left;
    private T right;

    public Pair(S left, T right) {
        this.left = left;
        this.right = right;
    }

    public S getLeft() {
        return left;
    }

    public void setLeft(S left) {
        this.left = left;
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair otherPair = (Pair) obj;
        return this.left.equals(otherPair.left) && this.right.equals(otherPair.right);
    }

    @Override
    public int hashCode() {
        return left.hashCode() * 12931 + right.hashCode();
    }
}
