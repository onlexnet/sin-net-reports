package sinnet;

import java.util.UUID;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;
import sinnet.write.ServiceActionAggregate;

public final class ServiceActionAggregateSpec {

  FixtureConfiguration<ServiceActionAggregate> fixture;

  @BeforeEach
  public void init() {
    fixture = new AggregateTestFixture<>(ServiceActionAggregate.class);
  }

  @Test
  public void testCommandHandlerCase() {
    val serviceActionId = UUID.randomUUID().toString();
    var cmd = Given
      .registerNewServiceActionCommand()
      .withServiceActionId(serviceActionId);

    var expected = new NewServiceRegistered(serviceActionId, cmd.getWhen());
    fixture
      .givenNoPriorActivity()
      .when(cmd)
      .expectEvents(expected);
  }
}