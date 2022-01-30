package sinnet.grpc;

import java.util.function.Consumer;

import io.vavr.Function1;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PropsBuilder {
  public static <T> PropSet<T> build(T builder) {
    return new PropSet<>(builder);
  }

  /**
   * Helper class for setting properties for proto3.
   * Proto3 allows not to set values, but id does not allow to set null values.
   * So special semantic is required to simplify operations.
   */
  public static class PropSet<T> {
    private final T builder;

    PropSet(T builder) {
      this.builder = builder;
    }

    /** Try set. Run setter only if value is not null. */
    public <U> PropSet<T> tset(U maybeValue, Function1<T, Consumer<U>> setter) {
      if (maybeValue == null) return this;
      setter.apply(builder).accept(maybeValue);
      return this ;
    }

    public T done() {
      return builder;
    }
  }

}
  