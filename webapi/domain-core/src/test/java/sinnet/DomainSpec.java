package sinnet;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;

/** Create and close report. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DomainSpec.AppContext.class)
@EnableAutoConfiguration
public class DomainSpec {

    @Autowired
    private CommandGateway gateway;

    @Autowired
    private QueryGateway queryGateway;


    @Test
    @SneakyThrows
    public void shouldInitialize() {
        var now = LocalDate.now();
        var cmd = Given
            .registerNewServiceActionCommand()
            .builder().when(now).build();

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
    static class AppContext {

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
        // public DuplicateResourceWithQualifier duplicateResourceWithQualifier1() {
        // return new DuplicateResourceWithQualifier() {
        // };
        // }

        // @Bean("qualifiedByName")
        // public DuplicateResourceWithQualifier duplicateResourceWithQualifier2() {
        // return new DuplicateResourceWithQualifier() {
        // };
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
