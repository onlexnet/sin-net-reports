package net.onlex;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({UniExtensions.class})
public class UserLogin {

  @Inject
  AzureAD azureAd;

  @Inject
  UserLoginProps userLoginProps;

  @Inject
  AppApi appApi;
  

  @When("I login using proper credentials")
  public void i_login_using_proper_credentials() {
    var userToken = azureAd.signIn(userLoginProps).sync();
    var bearerHeaderValue = String.format("Bearer %s", userToken);
    var projects = appApi.availableProjects(bearerHeaderValue);
    assertThat(projects).isEmpty();
  }

  @Then("I may get list of my projects")
  public void i_may_get_list_of_my_projects() {
    assertThat(azureAd).isNotNull();
  }

}
