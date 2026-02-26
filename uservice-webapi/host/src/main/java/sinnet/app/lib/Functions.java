package sinnet.app.lib;

import java.util.function.Function;

/**
 * Utility class for function helpers.
 */

public class Functions {

  /**
   * Creates a {@code Function} based on
   * <ul>
   *   <li><a href="https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html">method reference</a></li>
   *   <li><a href="https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html#syntax">lambda expression</a></li>
   * </ul>
   *
   * <p>
   * Examples (w.l.o.g. referring to Function):
   * <pre><code>// using a lambda expression
   * Function&lt;Integer, Integer&gt; add1 = Functions.of(i -&gt; i + 1);
   *
   * // using a method reference (, e.g. Integer method(Integer i) { return i + 1; })
   * Function&lt;Integer, Integer&gt; add2 = Functions.of(this::method);
   *
   * // using a lambda reference
   * Function&lt;Integer, Integer&gt; add3 = Functions.of(add1::apply);
   * </code></pre>
   *
   * <p>
   * <strong>Caution:</strong> Reflection loses type information of lambda references.
   * <pre><code>// type of a lambda expression
   * Type&lt;?, ?&gt; type1 = add1.getType(); // (Integer) -&gt; Integer
   *
   * // type of a method reference
   * Type&lt;?, ?&gt; type2 = add2.getType(); // (Integer) -&gt; Integer
   *
   * // type of a lambda reference
   * Type&lt;?, ?&gt; type3 = add3.getType(); // (Object) -&gt; Object
   * </code></pre>
   *
   * @param methodReference (typically) a method reference, e.g. {@code Type::method}
   * @param <R> return type
   * @param <T1> 1st argument
   * @return a {@code Function}
   */
  public static <T1, R> Function<T1, R> of(Function<T1, R> methodReference) {
    return methodReference;
  }

}
