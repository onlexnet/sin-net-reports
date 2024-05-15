package sinnet.infra;

import java.time.LocalDateTime;

import jakarta.annotation.Nonnull;
import reactor.core.Disposable;

/** Emits current time every second. */

public interface SecondsTicker {
  /** Invokes given handler on a virtual thread. */
  Disposable schedule(@Nonnull Handler timeHandler) throws InterruptedException;

  /** Interface definition for listeners. */
  @FunctionalInterface
  interface Handler {
    void on(LocalDateTime time);
  }
}

