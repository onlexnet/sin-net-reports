package sinnet.otp;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import reactor.core.Disposable;
import reactor.core.Disposables;
import sinnet.infra.SecondsTicker;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;

@RequiredArgsConstructor
@Component
class TotpGenerator implements OtpGenerator, AutoCloseable, SecondsTicker.Handler {

  private final SecondsTicker scheduler;
  private final Disposable.Composite instanceDisposer = Disposables.composite();
  private final AtomicBoolean processing = new AtomicBoolean(true);
  private final ConcurrentLinkedQueue<GenerationContext> generators = new ConcurrentLinkedQueue<>();

  record GenerationContext(LinkedBlockingQueue<Item.Valid> items, TOTPGenerator generator) { }

  interface Item {
    record Valid(String value, LocalDateTime when) implements Item {
    }

    enum Interrupted implements Item {
      INSTANCE
    }
  }

  @PostConstruct
  public void init() throws InterruptedException {
    scheduler.schedule(this);
    var scheduleDisposer = scheduler.schedule(this::on);
    instanceDisposer.add(scheduleDisposer);
  }

  @Override
  public Stream<String> values(String secret, int period) {

    var items = new LinkedBlockingQueue<Item.Valid>();

    var source = Spliterators.<Item>spliteratorUnknownSize(new Iterator<Item>() {

      @Override
      public boolean hasNext() {
        return processing.get();
      }

      @Override
      public Item next() {
        try {
          return items.take();
        } catch (InterruptedException e) {
          // int can occures when we 1) returned hasNext -> true and 2) curent work of waiting for data has been interrupted when waiting for new data
          processing.set(false);
          return Item.Interrupted.INSTANCE;
        }
      }

    }, Spliterator.IMMUTABLE);

    // TODO create shared instances of the generator
    var totpGenerator = new TOTPGenerator.Builder(secret.getBytes())
        .withHOTPGenerator(builder -> {
          builder.withPasswordLength(6);
          builder.withAlgorithm(HMACAlgorithm.SHA1); // SHA256 and SHA512 are also supported
        })
        .withPeriod(Duration.ofSeconds(period))
        .withClock(Clock.systemUTC())
        .build();
    var context = new GenerationContext(items, totpGenerator);
    generators.add(context);


    var result = StreamSupport.stream(source, false)
        .takeWhile(it -> it instanceof Item.Valid)
        .map(it -> switch (it) {
          case Item.Valid x -> x.value();
          default -> "impossible scenario";
        });
    
    return result.onClose(() -> generators.remove(context));
        
  }


  @Override
  public void close() {
    instanceDisposer.dispose();
  }

  @Override
  public void on(LocalDateTime time) {
    generators.forEach(it -> {
      var items = it.items();
      var generator = it.generator();
      var code = generator.at(time.toEpochSecond(ZoneOffset.UTC));
      items.add(new Item.Valid(code, time));
    });
    
  }

}
