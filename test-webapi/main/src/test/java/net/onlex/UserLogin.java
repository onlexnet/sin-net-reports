package net.onlex;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({UniExtensions.class})
public class UserLogin {

  @Inject
  AzureAD azureAd;

  @Inject
  UserLoginProps userLoginProps;

  AppApi appApi;
  
  @When("I am logged in as Operator1")
  public void i_am_logged_in_as_operator1() {
    var userToken = azureAd.signIn(userLoginProps).sync();
    var bearer = String.format("Bearer %s", userToken);
    appApi = TypesafeGraphQLClientBuilder.newBuilder()
      .header("Authorization", bearer)
      .build(AppApi.class);
  }

  @When("I login using proper credentials")
  public void i_login_using_proper_credentials() {
    var projects = appApi.availableProjects();
    assertThat(projects).isEmpty();
  }

  @Then("I may get list of my projects")
  public void i_may_get_list_of_my_projects() {
    assertThat(azureAd).isNotNull();
  }

  @When("I create new project")
  public void i_create_new_project() {
    appApi.Projects("my new project");
  }

  @Then("I may delete just created project")
  public void i_may_delete_just_created_project() {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

}
