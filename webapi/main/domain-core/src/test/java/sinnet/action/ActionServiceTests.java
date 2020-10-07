package sinnet.action;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import sinnet.ActionService;
import sinnet.AppTestContext;
import sinnet.Distance;
import sinnet.Name;
import sinnet.ServiceEntity;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class ActionServiceTests {

    @Autowired
    private ActionService sut;

    @Test
    @SneakyThrows
    public void myTest() {
        var now = LocalDate.of(2001, 2, 3);

        {
            var actual = sut.find(now, now);
            Assumptions.assumeThat(actual.block()).isEmpty();
        }

        var newEntity = ServiceEntity.builder()
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

            var expected = ServiceEntity.builder()
                .howFar(Distance.of(10))
                .when(now)
                .whom(Name.of("some Customer name"))
                .build();
            var actual = actualItems.head();
            Assertions.assertThat(actual.getValue()).isEqualTo(expected);

        }
    }
}
