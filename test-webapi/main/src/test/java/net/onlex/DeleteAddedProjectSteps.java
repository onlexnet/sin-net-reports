package net.onlex;

import com.google.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({UniExtensions.class})
public class DeleteAddedProjectSteps {

  @Inject
  AzureAD azureAd;

  @Inject
  UserLoginProps userLoginProps;

  @When("I create new project")
  public void i_create_new_project() {
    // var idToken = azureAd.signIn(userLoginProps).sync();
    // // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

  @Then("I may delete just created project")
  public void i_may_delete_just_created_project() {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

}
