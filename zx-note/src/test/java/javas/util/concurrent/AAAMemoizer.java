package javas.util.concurrent;

public class AAAMemoizer extends AbstractMemoizer<Long, Integer> implements AAA {
    private AAA delegate;

    protected AAAMemoizer(AAA delegate) {
        this.delegate = delegate;
    }

    @Override
    public Integer aaa(Long arg) {
        return compute(arg, (x) -> delegate.aaa(x));
    }
}
