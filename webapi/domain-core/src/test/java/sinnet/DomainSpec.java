package sinnet;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.config.AxonConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;

/** Create and close report. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DomainSpec.AppContext.class)
public class DomainSpec {

    /** fixme. */
    @Autowired
    private CommandGateway gateway;

    /** fixme. */
    @Autowired
    private QueryGateway queryGateway;


    /** fixme. */
    @Test
    @SneakyThrows
    public void shouldInitialize() {
        var now = LocalDate.now();
        var cmd = Given
            .createRegisterNewServiceAction()
            .toBuilder().when(now).build();

        gateway.sendAndWait(cmd);

        var ask = new RegisteredServices.Ask(now, null);
        var info = queryGateway
            .query(ask, RegisteredServices.Reply.class)
            .get();

        Assertions.assertThat(gateway).isNotNull();
        Assertions.assertThat(info.getEntries().length).isEqualTo(1);
    }

    /** Local config to override / produce locally required beans. */
    @Configuration
    @EnableAutoConfiguration
    @ComponentScan(basePackageClasses = {sinnet.PackageMarker.class})
    static class AppContext {

        // EmbeddedEventStore added yto avoid problems same as 
        // https://stackoverflow.com/questions/55706454/domain-event-entry-table-is-not-created-by-axon
        public EmbeddedEventStore eventStore(EventStorageEngine storageEngine, AxonConfiguration configuration) {
            return EmbeddedEventStore.builder()
                                     .storageEngine(storageEngine)
                                     .messageMonitor(configuration.messageMonitor(EventStore.class, "eventStore"))
                                     .build();
        }
        
        // @Bean
        // public CommandBus commandBus() {
        //     return SimpleCommandBus.builder().build();
        // }

        // @Bean(name = "annotatedHandler")
        // public AnnotatedEventHandlerWithResources createHandler() {
        // return new AnnotatedEventHandlerWithResources();
        // }

        // @Lazy
        // @Bean
        // public MissingResourceHandler missingResourceHandler() {
        // return new MissingResourceHandler();
        // }

        // @Lazy
        // @Bean
        // public DuplicateResourceHandler duplicateResourceHandler() {
        // return new DuplicateResourceHandler();
        // }

        // @Bean
        // @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
        // public PrototypeResource prototypeResource() {
        // return new PrototypeResource() {
        // };
        // }

        // @Bean
        // public EventBus eventBus() {
        // return SimpleEventBus.builder().build();
        // }
    }
}
