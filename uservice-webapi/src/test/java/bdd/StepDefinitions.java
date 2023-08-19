package bdd;

import static org.mockito.ArgumentMatchers.any;

import java.time.Duration;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.grpc.ProjectsGrpcAdapter;
import sinnet.grpc.projects.generated.ListReply;

public class StepDefinitions {

  @Autowired
  WebApplicationContext applicationContext;

  @MockBean
  ProjectsGrpcAdapter projectsGrpcService;

  @Autowired
  TestRestTemplate restTemplate;

  // @Before
  // public void before() {
  //   projectsGrpcService = Mockito.mock(ProjectsGrpcService.class);
  // }

  @When("user is requesting list of projects")
  public void user_is_requesting_list_of_projects() {

    var grpcResult = ListReply.newBuilder()
        .addProjects(sinnet.grpc.projects.generated.Project.newBuilder()
            .build())
        .build();
    Mockito
        .when(projectsGrpcService.list(any()))
        .thenReturn(grpcResult);

    // Mockito.when(nameProvider.getName()).thenReturn("my name");
    var rootUri = restTemplate.getRootUri();

    final var principalNameHeaderName = "X-MS-CLIENT-PRINCIPAL-NAME";
    final var principalNameHeaderId = "X-MS-CLIENT-PRINCIPAL-ID";
    var client = WebTestClient.bindToServer()
        .responseTimeout(Duration.ofMinutes(10))
        .baseUrl(rootUri + "/graphql")
        .defaultHeader(principalNameHeaderName, "principal-name")
        .defaultHeader(principalNameHeaderId, "principal-id")
        .build();

    var tester = HttpGraphQlTester.create(client);

    var actual = tester.documentName("namedProjects")
        .variable("projectName", "spring-framework")
        .execute()
        .path("Projects.list")
        .entityList(ProjectEntityGql.class)
        .hasSizeGreaterThan(0);

  }

  @Then("Response is returned")
  public void response_is_returned() {
  }

  @Then("Project uservice is requested")
  public void Project_uservice_is_requested() {
    // Write code here that turns the phrase above into concrete actions
  }


  
}
