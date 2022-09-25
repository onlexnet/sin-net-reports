package net.onlex;

import java.time.LocalDate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.onlex.support.UserEmail;
import net.onlex.support.Project;
import net.onlex.support.Sessions;

public class TimesheetsOperations {

  private Sessions sessions = new Sessions();

  @Given("a project called {projectName} created by {userName}")
  public void a_project_called_term1_created_by_term2(Project project, UserEmail owner) {
    var ctx = sessions.getOrCreate(owner);
    var saveProjectResult = ctx.appApi.saveProject(project.getName());
    // ??? Why is success
  }

  @When("{userName} creates timeentry for {projectName}")
  public void operator1_creates_timeentry_for_project1(UserEmail operator, Project project) {
      // Write code here that turns the phrase above into concrete actions
      throw new io.cucumber.java.PendingException("{userName} creates timeentry for {projectName}");
  }

  @Then("operation is rejected")
  public void operation_is_rejected() {
      // Write code here that turns the phrase above into concrete actions
      throw new io.cucumber.java.PendingException("operation is rejected");
  }

  @Then("number of timesheets in {projectName} is zero")
  public void number_of_timesheets_in_project_is_zero(Project project) {
      // Write code here that turns the phrase above into concrete actions
      throw new io.cucumber.java.PendingException("number of timesheets in project {projectName} is zero");
  }

  @When("User {userName} assigns {userName} to {projectName}")
  public void owner_assigns_operator_to_project(UserEmail owner, UserEmail operator, Project project) {
    var ownerSession = sessions.getOrCreate(owner);
    var projectData = ownerSession.appApi.projectList(project.getName());
    var projectId = projectData.getList().get(0).getEntity().entityId;
    var result = ownerSession.appApi.newAction(projectId, LocalDate.now());
  }

}
