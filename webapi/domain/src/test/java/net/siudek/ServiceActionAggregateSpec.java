package net.siudek;

import java.util.UUID;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.val;

public final class ServiceActionAggregateSpec {

  FixtureConfiguration<ServiceActionAggregate> fixture;

  @BeforeEach
  public void init() {
    fixture = new AggregateTestFixture<>(ServiceActionAggregate.class);
  }

  @Test
  public void testCommandHandlerCase() {
    val serviceActionId = UUID.randomUUID().toString();
    fixture
      .givenNoPriorActivity()
      .when(Given.registerNewServiceCommand().withServiceActionId(serviceActionId))
      .expectEvents(new NewServiceRegistered(serviceActionId));
  }
}