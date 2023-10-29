package sinnet.features;

import java.util.Stack;

import org.assertj.core.api.Assertions;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import sinnet.models.ValName;

@RequiredArgsConstructor
public class ProjectsStepDefinition {

  private final TestApi testApi;
  private final ClientContext ctx;

  private Stack<Statement> statements = new Stack<>();

  @When("{operatorAlias} asks about available projects")
  public void operator_asks_about_available_projects(ValName operatorAlias ) {
    var result = testApi.numberOfProjects(operatorAlias);
    statements.push(new NumberOfProjects(result));
  }

  @Then("list of projects is not null")
  public void list_of_projects_is_not_null() {
    var statement = statements.pop();
    var typed = (NumberOfProjects) statement;

    Assertions.assertThat(typed.value).isGreaterThan(0);
  }

  @Then("list of projects is empty")
  public void list_of_projects_is_empty() {
    var statement = statements.pop();
    var typed = (NumberOfProjects) statement;

    Assertions.assertThat(typed.value).isZero();
  }

  sealed interface Statement {
  }

  record NumberOfProjects(int value) implements Statement {
  }
}
