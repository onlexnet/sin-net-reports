package sinnet.features;

import java.io.ByteArrayInputStream;
import java.util.zip.ZipInputStream;

import org.assertj.core.api.Assertions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class TimeentriesStepDefinitions {

  private final TestApi testApi;
  private final ClientContext ctx;

  @Given("a new project called {projectAlias} created by {operatorAlias}")
  public void a_new_project_called(ValName projectAlias, ValName operatorAlias) {
    testApi.newProject(ctx, operatorAlias, projectAlias);
  }

  @Given("an operator called {operatorAlias} assigned to project called {projectAlias}")
  public void an_operator_called_alias1_assigned_toproject_called_alias2(ValName operatorAlias, ValName projectAlias) {
    testApi.assignOperator(ctx, operatorAlias, projectAlias);
  }

  @When("{operatorAlias} creates new timeentry for {projectAlias}")
  public void operator_called_creates_new_timeentry(ValName operatorAlias, ValName projectAlias) {
    testApi.createEntry(ctx, projectAlias, operatorAlias);
  }

  @Then("operation succeeded")
  public void operation_succeeded() {
    // noe checks yet
  }

  @Then("the new timeentry is visible on the {projectAlias}")
  public void the_new_timeentry_is_visible_on_the_projectAlias(ValName projectAlias) {
    var latestTimeentryId = ctx.latestTimeentryId();
    var timeentryCtx = ctx.known().timeentries().get(latestTimeentryId);
    var items = testApi.listTimeentries(ctx, projectAlias, timeentryCtx.when());
    Assertions.assertThat(items).contains(latestTimeentryId);
  }

  @When("{operatorAlias} requests report1 pack for {projectAlias}")
  @SneakyThrows
  public void operator1_requests_report1_pack(ValName operatorAlias, ValName projectAlias) {

    testApi.createEntry(ctx, projectAlias, operatorAlias);

    var items = testApi.requestReport1Pack(ctx);
    var input = new ByteArrayInputStream(items);
    var asStream = new ZipInputStream(input);
    var entry = asStream.getNextEntry();
  }

  @Then("report1 pack is returned")
  public void report1_pack_is_returned() {
  }

  @When("create two secrets on the timeentry")
  public void create_two_secrets_on_the_timeentry() {
    testApi.addSecretExToUpdatedCustomer(ctx);
    testApi.addSecretExToUpdatedCustomer(ctx);
  }

  @Then("update time on secrets is the same")
  public void update_time_on_secrets_is_the_same() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("update one of the secrets")
  public void update_one_of_the_secrets() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("update time on secrets is different")
  public void update_time_on_secrets_is_different() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

}
