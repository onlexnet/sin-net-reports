package sinnet;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;
import reactor.core.publisher.EmitterProcessor;
import sinnet.appevents.ServicesProjection;
import sinnet.read.DailyReports;

/** Create and close report. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppTestContext.class, DomainSpec.class })
@EnableAutoConfiguration
public class DomainSpec {

    /** fixme. */
    @Autowired
    private CommandGateway gateway;

    /** fixme. */
    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private AppEvents appEvents;

    /** fixme. */
    @Test
    @SneakyThrows
    public void shouldInitialize() {
        var now = LocalDate.now();
        var cmd = Given.createRegisterNewServiceAction().toBuilder().when(now).build();

        gateway.sendAndWait(cmd);

        appEvents.notifications.blockFirst();

        var ask = new DailyReports.Ask(now);
        var actual = (DailyReports.Reply.Some) queryGateway
            .query(ask, DailyReports.Reply.class)
            .get();

        Assertions.assertThat(actual.getEntries().size()).isEqualTo(1);
    }


    @Bean
    @Scope("prototype")
    public static AppEvents appEvents() {
        return new AppEvents();
    }

    static class AppEvents {
        private EmitterProcessor<ServicesProjection.Changed> notifications = EmitterProcessor.create();

        public void onApplicationEvent(ServicesProjection.Changed event) {
            notifications.onNext(event);
        }
    }
}
