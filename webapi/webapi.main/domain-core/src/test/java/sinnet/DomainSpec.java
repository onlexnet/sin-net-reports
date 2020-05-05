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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.ReplayProcessor;
import sinnet.appevents.ServicesProjection;
import sinnet.read.DailyReports;

/** Create and close report. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppTestContext.class, DomainSpec.TestContext.class })
@EnableAutoConfiguration
public class DomainSpec {

    /** fixme. */
    @Autowired
    private CommandGateway gateway;

    /** fixme. */
    @Autowired
    private QueryGateway queryGateway;

    @Autowired
    private TestContext.AppEvents appEvents;

    /** fixme. */
    @Test
    public void shouldInitialize() {
        var now = LocalDate.now();
        var cmd = Given.createRegisterNewServiceAction().toBuilder().when(now).build();

        gateway.sendAndWait(cmd);

        appEvents.notifications.blockFirst();

        var ask = new DailyReports.Ask(now);
        var actual = (DailyReports.Reply.Some) queryGateway
            .query(ask, DailyReports.Reply.class)
            .join();

        Assertions.assertThat(actual.getEntries().size()).isEqualTo(1);
    }


    @Configuration
    static class TestContext {

        @Bean
        public static AppEvents appEvents() {
            return new AppEvents();
        }
    
        static class AppEvents {
            private ReplayProcessor<ServicesProjection.Changed> notifications = ReplayProcessor.create();
    
            @EventListener
            public void onApplicationEvent(ServicesProjection.Changed event) {
                notifications.onNext(event);
            }
        }
    }
}
