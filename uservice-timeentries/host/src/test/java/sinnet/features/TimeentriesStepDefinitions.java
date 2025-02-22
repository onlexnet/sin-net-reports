package sinnet.features;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import org.assertj.core.api.Assertions;
import org.testcontainers.shaded.com.google.common.base.Objects;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.features.RpcApi.UseAlias;
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
    testApi.createEntry(ctx, UseAlias.of(operatorAlias), projectAlias);
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

    testApi.createEntry(ctx, UseAlias.of(operatorAlias), projectAlias);

    var items = testApi.requestReport1Pack(ctx);
    var input = new ByteArrayInputStream(items);
    var asStream = new ZipInputStream(input);
    var entry = asStream.getNextEntry();
  }

  @Then("report1 pack is returned")
  public void report1_pack_is_returned() {
  }

  @When("create two secrets on the customer")
  public void create_two_secrets_on_the_customer() {
    testApi.addSecretToUpdatedCustomer(ctx, 2);
  }

  @Then("update time on secrets is the same")
  public void update_time_on_secrets_is_the_same() {
    var customerModel = testApi.getCustomer(ctx, UseAlias.current(), UseAlias.current());
    var secrets = customerModel.getSecrets();
    var firstSecret = secrets.getFirst();
    var updateTime = firstSecret.getChangedWhen();
    Assertions.assertThat(secrets).allSatisfy(it -> Assertions.assertThat(it.getChangedWhen()).isEqualTo(updateTime));
  }

  @Then("update one of the secrets")
  public void update_one_of_the_secrets() {
    var customerAlias = testApi.rpcApi.getCurrentCustomer(UseAlias.current());
    var customerModel = testApi.getCustomer(ctx, UseAlias.current(), UseAlias.current());
    var secrets = customerModel.getSecrets();
    var first = secrets.getFirst();
    first.setUsername("new username: " + UUID.randomUUID());

    // as we would like to see update time, lets wait with saving when current time will be different than last secret update
    var firstTime = first.getChangedWhen();
    while (true) {
      var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      if (!Objects.equal(firstTime, now)) break;
    }

    testApi.updateCustomer(ctx, customerAlias, customerModel);
  }

  @Then("update time on secrets is different")
  public void update_time_on_secrets_is_different() {
    var customer = testApi.rpcApi.getCurrentCustomer(UseAlias.current());
    var model = testApi.getCustomer(ctx, UseAlias.of(customer), UseAlias.current());
    var secretsByTime = model.getSecrets().stream().collect(Collectors.groupingBy(it -> it.getChangedWhen(), Collectors.counting()));

    Assertions.assertThat(secretsByTime)
      .as("A Customer have extend-secrets with various update time")
      .hasSizeGreaterThan(1);
  }

}
