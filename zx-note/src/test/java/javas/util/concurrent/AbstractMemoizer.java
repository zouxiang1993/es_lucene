package javas.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;
import java.util.function.Function;

/**
 * 为一些接收输入类型Req, 返回输出类型Resp的接口提供缓存的功能
 * <p>
 * 通过以下步骤为类Clazz的一个方法 "Resp fun(Req)" 提供缓存的功能:
 * <pre>
 *     1.创建一个新类XxxMemoizer集成AbstractMemoizer, 并且实现和Clazz相同的接口
 *     2.XxxMemoizer在构造时应该要接收一个Clazz实例，用来委托处理实际的工作
 *     3.XxxMemoizer的fun(..)方法应该调用父类的compute方法，传入输入参数和实际的处理函数的lamda表达式
 * </pre>
 *
 * 例如:
 * <pre>
 *     public interface BBB {
 *          Double bbb(String arg);
 *     }
 *
 *
 *     public class BBBMemoizer extends AbstractMemoizer<String, Double> implements BBB {
 *
 *          private BBB delegate;
 *
 *          protected BBBMemoizer(BBB delegate) {
 *              this.delegate = delegate;
 *          }
 *
 *          public Double bbb(String arg) {
 *               return compute(arg, (x) -> delegate.bbb(x));
 *          }
 *      }
 *
 * </pre>
 * 也可以同时为多个方法提供缓存的功能，前提是要保证方法之间的key不会有冲突
 *
 */
public abstract class AbstractMemoizer<Req, Resp> {
    protected final ConcurrentHashMap<Req, FutureTask<Resp>> taskMap = new ConcurrentHashMap<>();

    /**
     *
     * @param request 输入参数
     * @param function 实际的处理函数
     * @return
     */
    protected Resp compute(Req request, Function<Req, Resp> function) {
        while (true) {
            // TODO: 大多数情况下应该不是直接用Req作为taskMap的key
            // Req应该要可以实现某个接口， 提供一个 "K memoizerKey()"方法来计算在taskMap中的key
            // 当Req实现该接口时，使用memoizerKey生成的结果当做key;
            // 当Req没有实现该接口时，直接用Req自身当做key
            FutureTask<Resp> runningTask = taskMap.get(request);
            if (runningTask == null) {
                Callable<Resp> callable = new Callable<Resp>() {
                    @Override
                    public Resp call() throws Exception {
                        // TODO: 这里的异常是否需要处理？
                        return function.apply(request);
                    }
                };
                FutureTask<Resp> newTask = new FutureTask<>(callable);
                FutureTask<Resp> previous = taskMap.putIfAbsent(request, newTask);
                if (previous == null) {
                    newTask.run();
                    runningTask = newTask;
                }
            }
            try {
                return runningTask.get();
                // TODO: 获取到结果之后马上清除在taskMap中的key
                // 或者引入一个定时清除的功能
            } catch (CancellationException e) {
                taskMap.remove(request);
            } catch (Exception e) {
                // TODO: 加入一个retry提供失败重试的功能
                throw new RuntimeException(e);
            }
        }
    }
}
