package net.onlex;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import javax.inject.Inject;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod({UniExtensions.class})
public class UserLogin {

  @Inject
  MyService myService;

  @Inject
  UserLoginProps userLoginProps;

  @Inject
  AppApi appApi;
  

  @When("I login using proper credentials")
  public void i_login_using_proper_credentials() {
    var userToken = myService.signIn(userLoginProps).sync();
    var bearerHeaderValue = String.format("Bearer %s", userToken);
    var projects = appApi.availableProjects(bearerHeaderValue);
    assertThat(projects).isEmpty();
  }

  @Then("I may get list of my projects")
  public void i_may_get_list_of_my_projects() {
    assertThat(myService).isNotNull();
  }

}

class UniExtensions {
  public static <T> T sync(Uni<T> async) {
    return async.await().atMost(Duration.ofMinutes(1));
  }
}