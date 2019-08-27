package javas.util.concurrent;

public class BBBMemoizer extends AbstractMemoizer<String, Double> implements BBB {

    private BBB delegate;

    protected BBBMemoizer(BBB delegate) {
        this.delegate = delegate;
    }

    @Override
    public Double bbb(String arg) {
        return compute(arg, (x) -> delegate.bbb(x));
    }
}
