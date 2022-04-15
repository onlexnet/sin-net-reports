package net.onlex;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java8.En;

public class UserLogin
    implements En {

  @Inject
  MyService myService;

  @When("I login using proper credentials")
  public void i_login_using_proper_credentials() {
    Assertions.assertThat(myService).isNotNull();
  }

  @Then("I may get list of my projects")
  public void i_may_get_list_of_my_projects() {
    Assertions.assertThat(myService).isNotNull();
  }

}
