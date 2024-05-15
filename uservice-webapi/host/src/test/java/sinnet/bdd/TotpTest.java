package sinnet.bdd;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.TOTPGenerator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.Disposable;
import reactor.core.Disposables;
import sinnet.infra.InfraConfiguration;
import sinnet.infra.SecondsTicker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfraConfiguration.class )
public class TotpTest {

  static String exampleSecret = "JERAN5FEJNCN7C44YJCXAG6CI3MGPDNK63N3WPYKAVYUMHQ4FKP5VDOU6VLGU6PR";
  @Autowired
  SecondsTicker secondsTicker;


  @Test
  void shouldGenerateProperCode() throws InterruptedException {

    var when = LocalDateTime
        .of(2001, 2, 3, 4, 5, 6)
        .toInstant(ZoneOffset.UTC);

    TOTPGenerator totp = new TOTPGenerator.Builder(exampleSecret.getBytes())
        .withHOTPGenerator(builder -> {
          builder.withPasswordLength(6);
          builder.withAlgorithm(HMACAlgorithm.SHA1); // SHA256 and SHA512 are also supported
        })
        .withPeriod(Duration.ofSeconds(30))
        .withClock(Clock.fixed(Instant.from(when), ZoneOffset.UTC))
        .build();

    Assertions.assertThat(totp.now()).isEqualTo("135172");
  }

  @Test
  @SneakyThrows
  @Timeout(value = 3)
  void shouldGenerateValuesWithTime() {

    var sut = new TotpGenereator(secondsTicker);
    var actual = sut.values("my secret", 42);

    var first = actual.findFirst();

    Assertions.assertThat(first).isNotEmpty();
  }


  @RequiredArgsConstructor
  class TotpGenereator implements AutoCloseable, SecondsTicker.Handler {

    private final SecondsTicker scheduler;
    private final Disposable.Composite instanceDisposer = Disposables.composite();
    private final LinkedBlockingQueue<Item.Valid> items = new LinkedBlockingQueue<>();
    private final AtomicBoolean processing = new AtomicBoolean(true);

    interface Item {
      record Valid(String value, LocalDateTime when) implements Item { }
      enum Interrupted implements Item { INSTANCE }
    }

    Stream<String> values(String secret, int period) throws InterruptedException {
      // TODO test multiple invocations in terms of releasing resources or support only one invocation

      scheduler.schedule(this);
      var scheduleDisposer = scheduler.schedule(this::on);

      instanceDisposer.add(scheduleDisposer);

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
            processing.set(false);
            return Item.Interrupted.INSTANCE;
          }
        }
        
      }, Spliterator.IMMUTABLE);

      return StreamSupport.stream(source, false)
        .takeWhile(it -> it instanceof Item.Valid)
        .map(it -> switch (it) {
          case Item.Valid x -> x.value();
          default -> "impossible scenario";
    });

    }

    @Override
    public void close() {
      instanceDisposer.dispose();
    }

    @Override
    public void on(LocalDateTime time) {
      items.add(new Item.Valid("aaaaaaaaaaaaa", time));
    }
  }
}

