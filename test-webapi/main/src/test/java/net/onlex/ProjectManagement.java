package net.onlex;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Base64;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import io.smallrye.jwt.build.Jwt;
import io.vavr.collection.HashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import net.onlex.AppApi.ProjectEntity;
import net.onlex.ProjectManagement.UserContext;
import net.onlex.support.UserEmail;

@ExtensionMethod({ UniExtensions.class })
public class ProjectManagement {

  @Inject
  AzureAD azureAd;

  @Inject
  UserLoginProps userLoginProps;

  private Sessions session = new Sessions();

  @Data
  @AllArgsConstructor
  static class UserContext {
    AppApi appApi;
    ProjectEntity lastProject;
    Integer knownNumberOfProjects;
  }

  @Then("I may delete just created project")
  public void i_may_delete_just_created_project() {
    // Write code here that turns the phrase above into concrete actions
    // throw new io.cucumber.java.PendingException();
  }

  @Then("Number of projects is {int}")
  public void number_of_projects_is(int numberOfProjects) {
    var ctx = session.getActiveUser();
    var actual = ctx.appApi.projectsCount().getNumberOfProjects();
    assertThat(actual).isEqualTo(numberOfProjects);
  }

  @Then("the project is visible on the list of projects")
  public void the_project_is_visible_on_the_list_of_projects() {
    var ctx = session.getActiveUser();
    var projectName = ctx.lastProject;
    var expectedName = projectName.getName();
    var projects = ctx.appApi.projectList(expectedName);
    assertThat(projects.getList().stream().map(it -> it.getName())).contains(expectedName);
  }

  @When("User {userName} creates new project")
  public void user_creates_new_project(UserEmail email) {
    var projectName = "My new project [" + RandomStringUtils.randomAlphabetic(4) + "]";
    var ctx = session.get(email);
    var projectEntity = ctx.appApi.saveProject(projectName).getSave();
    ctx.lastProject = projectEntity;
  }

  @When("User {userName} deletes lastly created project")
  public void user_deletes_lastly_create_project(UserEmail email) {
    var ctx = session.get(email);
    var id = ctx.lastProject;
    var projectId = new AppApi.ProjectId(id.getEntity().getEntityId(), 0);
    ctx.appApi.removeProject(projectId);
  }

}

final class Sessions {

  /** Context of the user, wh lastly did some external operation  */
  @Getter
  private UserContext activeUser = null;

  private HashMap<UserEmail, UserContext> users = HashMap.empty();

  public UserContext active;
  public UserContext get(UserEmail userEmail) {
    if (users.containsKey(userEmail)) {
      var active = users.get(userEmail).get();
      activeUser = active;
    } else {
      var secret = "my super secret key to sign my dev JWT token";
      var keyBytes = new String(Base64.getEncoder().encode(secret.getBytes()));
      var builder = Jwt.claims();
      var token = builder
          .issuer("https://issuer.org")
          .claim("emails", List.of(userEmail.getEmail()))
          .signWithSecret(keyBytes);
      var bearer = String.format("Bearer %s", token);
      var appApi = TypesafeGraphQLClientBuilder.newBuilder()
          .header("Authorization", bearer)
          .build(AppApi.class);
      var active = new UserContext(appApi, null, 0);
      users = users.put(userEmail, active);
      activeUser = active;
    }

    return activeUser;
  }
}
