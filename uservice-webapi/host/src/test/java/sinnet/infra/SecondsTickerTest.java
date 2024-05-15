package sinnet.infra;

import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfraConfiguration.class)
@Timeout(value = 5)
public class SecondsTickerTest {
  
  @Autowired
  SecondsTicker secondsTicker;

  enum ThreadType {
    UNDEFINED,
    VIRTUAL,
    NON_VIRTUAL
  }

  @Test
  @SneakyThrows
  void shouldInvokeUsingVirtualThread() {

    var threadType = new AtomicReference<ThreadType>(ThreadType.UNDEFINED);
    var waiter = new Semaphore(-1);
    SecondsTicker.Handler h = time -> {
      var result = Thread.currentThread().isVirtual()
          ? ThreadType.VIRTUAL
          : ThreadType.NON_VIRTUAL;
      threadType.compareAndSet(ThreadType.UNDEFINED, result);
      waiter.release();
    };
    secondsTicker.schedule(h);

    waiter.acquire();

    Assertions.assertThat(threadType.get()).isEqualTo(ThreadType.VIRTUAL);
  }

  @Test
  void shouldNotInvokeWhenThePreviousInvocationIsUnfinished() {
    // TBD
  }

  @Test
  void shouldEmitExpectedValues() throws InterruptedException {
    LocalDateTime now;
    while (true) {
      // lets wait when it is close to half a second
      now = LocalDateTime.now();
      var nanos = now.getNano(); // 0 to 999,999,999
      if ((nanos > 400_000_000) && (nanos < 600_000_000)) {
        break;
      }
    }

    var waiter = new Semaphore(-2);
    var handled = new LinkedBlockingQueue<LocalDateTime>();
    SecondsTicker.Handler h = time -> {
      handled.add(time);
      waiter.release();
    };
    secondsTicker.schedule(h);

    waiter.acquire();
    var expected1 = now.plusSeconds(1).withNano(0);
    Assertions.assertThat(handled).contains(
      expected1,
      expected1.plusSeconds(1));
  }
}
