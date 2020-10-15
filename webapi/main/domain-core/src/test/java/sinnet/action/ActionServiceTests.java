package sinnet.action;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import reactor.core.publisher.Mono;
import sinnet.ActionService;
import sinnet.AppTestContext;
import sinnet.Dates;
import sinnet.ServiceValue;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.Name;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class ActionServiceTests {

    @Autowired
    private ActionService sut;

    @Test
    public void myTest() {
        var now = Dates.gen().head();

        {
            var actual = sut.find(now, now);
            Assumptions.assumeThat(actual.block()).isEmpty();
        }

        var newEntity = ServiceValue.builder()
            .howFar(Distance.of(10))
            .when(now)
            .whom(Name.of("some Customer name"))
            .build();

        Mono.when(
            sut.save(UUID.randomUUID(), newEntity),
            sut.save(UUID.randomUUID(), newEntity),
            sut.save(UUID.randomUUID(), newEntity)
        ).block();

        {
            var actualItems = sut.find(now, now).block();
            Assertions.assertThat(actualItems).hasSize(3);

            var expected = ServiceValue.builder()
                .howFar(Distance.of(10))
                .when(now)
                .whom(Name.of("some Customer name"))
                .build();
            var actual = actualItems.head();
            Assertions.assertThat(actual.getValue()).isEqualTo(expected);

        }
    }

    @Test
    public void shouldSaveFullModel() {
        var now = Dates.gen().head();

        var newEntity = ServiceValue.builder()
            .who(Email.of("some person"))
            .howFar(Distance.of(1))
            .howLong(ActionDuration.of(2))
            .what("some action")
            .when(now)
            .whom(Name.of("some Customer name"))
            .build();

        assertThat(sut.save(UUID.randomUUID(), newEntity).block()).isTrue();

        var actual = sut.find(now, now).block().head().getValue();
        var expected = ServiceValue.builder()
            .who(Email.of("some person"))
            .howFar(Distance.of(1))
            .howLong(ActionDuration.of(2))
            .what("some action")
            .when(now)
            .whom(Name.of("some Customer name"))
            .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldUpdateFullModel() {
        var now = Dates.gen().head();

        var newEntity = ServiceValue.builder()
            .when(Dates.gen().head())
            .build();
        var updateEntity = ServiceValue.builder()
            .who(Email.of("some person"))
            .howFar(Distance.of(1))
            .howLong(ActionDuration.of(2))
            .what("some action")
            .when(now)
            .whom(Name.of("some Customer name"))
            .build();

        var entityId = UUID.randomUUID();
        sut.save(entityId, newEntity).block();
        assertThat(sut.update(entityId, updateEntity).block()).isTrue();

        var actual = sut.find(now, now).block().head().getValue();
        var expected = ServiceValue.builder()
            .who(Email.of("some person"))
            .howFar(Distance.of(1))
            .howLong(ActionDuration.of(2))
            .what("some action")
            .when(now)
            .whom(Name.of("some Customer name"))
            .build();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldSaveMinModel() {
        var now = Dates.gen().head();

        var newEntity = ServiceValue.builder()
            .when(now)
            .build();

        var entityId = UUID.randomUUID();
        assertThat(sut.save(entityId, newEntity).block()).isTrue();

        var actual = sut.find(entityId).block().getValue();
        var expected = ServiceValue.builder()
            .when(now)
            .build();
        assertThat(actual).isEqualTo(expected);
    }
}
