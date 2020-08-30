package sinnet;

import java.util.UUID;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;
import sinnet.events.NewServiceActionRegistered;
import sinnet.write.ServiceActionsAggregate;

public final class ServiceActionAggregateSpec {

  FixtureConfiguration<ServiceActionsAggregate> fixture;

  @BeforeEach
  public void init() {
    fixture = new AggregateTestFixture<>(ServiceActionsAggregate.class);
  }

  @Test
  public void testCommandHandlerCase() {
    val serviceActionId = UUID.randomUUID().toString();
    var cmd = Given
      .createRegisterNewServiceAction()
      .withServiceActionId(serviceActionId);

    var expected = new NewServiceActionRegistered(serviceActionId, cmd.getWhen(), "what");
    fixture
      .givenNoPriorActivity()
      .when(cmd)
      .expectEvents(expected);
  }
}