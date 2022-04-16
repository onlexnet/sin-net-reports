package net.onlex.belly;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java8.En;
import net.onlex.Belly;

// import io.cucumber .java.en.Given;
// import io.cucumber.java.en.When;

public class StepDefinitions implements En {

  private Belly belly;

  public StepDefinitions() {

    Given("I have {int} cukes in my belly", (Integer int1) -> {
      belly = new Belly();
      belly.eat(int1);
    });

    When("I wait {int} hours", (Integer hours) -> {
      belly.timeInHrs(hours);
    });

    Then("my belly should growl", () -> {
      assertThat(belly.growl()).isTrue();
    });
  }

}
