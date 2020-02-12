package sinnet.write;

import java.time.Duration;
import java.time.LocalDate;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.Test;

import sinnet.Distance;
import sinnet.Name;
import sinnet.RegisterNewServiceAction;
import sinnet.events.NewServiceRegistered;

/** unit tests for ServiceActionAggregate. */
public class ServiceActionAggregateTests {

    /** Allows to definei testing scenarios. */
    private FixtureConfiguration<ServiceActionAggregate> fixture;

    /** initializes test fixture. */
    public ServiceActionAggregateTests() {
        fixture = new AggregateTestFixture<>(ServiceActionAggregate.class);
    }

    /** Register a new service action. */
    @Test
    public void shouldBeRegisterable() {
        var register = new RegisterNewServiceAction(
            "my id",
            Name.of("who"),
            LocalDate.of(2001, 2, 3),
            Name.of("whom"),
            "what",
            Duration.ofHours(1),
            Distance.of(2));

        var registered = new NewServiceRegistered(
            "my id",
            LocalDate.of(2001, 2, 3));

        fixture
            .givenNoPriorActivity()
            .when(register)
            .expectEvents(registered);
    }

}
