package sinnet;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import sinnet.gql.models.ProjectEntityGql;
import sinnet.grpc.ProjectsGrpcAdapter;
import sinnet.grpc.projects.generated.ListReply;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Program.class)
@ActiveProfiles(Profiles.TEST)
@AutoConfigureMockMvc
class GqlTest {

  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  WebApplicationContext applicationContext;

  @MockBean
  ProjectsGrpcAdapter projectsGrpcService;

  @Test
  void doSomething() {

    var grpcResult = ListReply.newBuilder()
        .addProjects(sinnet.grpc.projects.generated.Project.newBuilder()
          .build())
        .build();
    Mockito
      .when(projectsGrpcService.list(any()))
      .thenReturn(grpcResult);

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
}
