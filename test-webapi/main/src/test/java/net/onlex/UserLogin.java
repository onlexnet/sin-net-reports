package net.onlex;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java8.En;

public class UserLogin
    implements En {

  @Inject
  MyService myService;

  @ConfigProperty(name = "OPERATOR1_NAME")
  String operator1Name;
  
  @ConfigProperty(name = "OPERATOR1_PASSWORD")
  String operator1Password;

  @When("I login using proper credentials")
  public void i_login_using_proper_credentials() {
    Assertions.assertThat(myService).isNotNull();
    Assertions.assertThat(operator1Name).isNotNull();
  }

  @Then("I may get list of my projects")
  public void i_may_get_list_of_my_projects() {
    Assertions.assertThat(myService).isNotNull();
  }

}
