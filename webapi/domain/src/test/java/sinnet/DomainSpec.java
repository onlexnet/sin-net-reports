package sinnet;

import org.assertj.core.api.Assertions;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DomainSpec.AppContext.class)
public class DomainSpec {

    @Autowired
    private CommandBus commandBus;

    @Test
    public void shouldInitialize() {
        Assertions.assertThat(commandBus).isNotNull();
    }

    // @AnnotationDriven
    @Configuration
    static class AppContext {

        @Bean
        public CommandBus commandBus() {
            return SimpleCommandBus.builder().build();
        }

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
