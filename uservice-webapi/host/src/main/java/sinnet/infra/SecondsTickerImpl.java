package sinnet.infra;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
class SecondsTickerImpl implements SecondsTicker, AutoCloseable {

  private final Flux<Long> timer = Flux.interval(computeDelay(), Duration.ofSeconds(1));
  private final Disposable.Composite instanceDisposer = Disposables.composite();
  private final List<Handler> handlers = new CopyOnWriteArrayList<>();

  @PostConstruct
  public void init() {
    var timeSubscription = timer.subscribe(new TimerListener());
    instanceDisposer.add(timeSubscription);
  }

  // calculates how long we need to wait for full time when part of miliseconds is
  // close to 0.
  private Duration computeDelay() {
    var now = LocalDateTime.now();
    var whenStart = now.plusSeconds(1).withNano(0);
    return Duration.between(now, whenStart);
  }

  @Override
  public void close() {
    instanceDisposer.dispose();
  }

  @Override
  public Disposable schedule(Handler timeHandler) {
    handlers.add(timeHandler);
    return () -> handlers.remove(timeHandler);
  }

  private final ExecutorService virtualTask =  Executors.newVirtualThreadPerTaskExecutor();
  class TimerListener implements Consumer<Long> {

    @Override
    public void accept(Long t) {
      virtualTask.submit(() -> {
        for (var handler : handlers) {
          handler.on(LocalDateTime.now());
        }
      });
    }
  }
}
