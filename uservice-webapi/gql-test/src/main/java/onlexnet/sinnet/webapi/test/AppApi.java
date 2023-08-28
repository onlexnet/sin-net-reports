package onlexnet.sinnet.webapi.test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.graphql.test.tester.GraphQlTester.Entity;
import org.springframework.graphql.test.tester.GraphQlTester.EntityList;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.SneakyThrows;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.gql.models.UserGql;

/** Set of methods used to test application functionlity. */
public class AppApi {

  private final HttpGraphQlTester tester;

  /** TBD. */
  @SneakyThrows
  public AppApi(String rootUri, String email) {
    final var principalNameHeaderName = "X-MS-CLIENT-PRINCIPAL-NAME";
    final var principalNameHeaderId = "X-MS-CLIENT-PRINCIPAL-ID";
    var client = WebTestClient.bindToServer()
        .responseTimeout(Duration.ofMinutes(10))
        .baseUrl(rootUri + "/graphql")
        .defaultHeader(principalNameHeaderName, email)
        .defaultHeader(principalNameHeaderId, "principal-id")
        .build();

    tester = HttpGraphQlTester.create(client);
  }

  /** TBD. */
  public EntityList<ProjectEntityGql> findProjectByName(String projectName) {

    return tester.documentName("namedProjects")
        .variable("projectName", projectName)
        .execute()
        .path("Projects.list")
        .entityList(ProjectEntityGql.class);
  }

  /** TBD. */
  public Entity<ProjectEntityGql, ?> createProject(String projectName) {
    return tester.documentName("projectCreate")
        .variable("name", projectName)
        .execute()
        .path("Projects.save")
        .entity(ProjectEntityGql.class);
  }

  /** TBD. */
  public Entity<Integer, ?> numberOfProjects() {
    return tester.documentName("numberOfProjects")
        .execute()
        .path("Projects.numberOfProjects")
        .entity(Integer.class);
  }

  /**
   * Invokes Customers.reserve
   */
  public Entity<SomeEntityGql, ?> reserveCustomer(String projectId) {
    return tester.documentName("reserveCustomer")
        .variable("projectId", projectId)
        .execute()
        .path("Customers.reserve")
        .entity(SomeEntityGql.class);
  }

  /** REturns list of all users (emails) related to given project. */
  public Entity<List<UserGql>, ?> searchUsers(String projectId) {
    return tester.documentName("usersSearch")
        .variable("projectId", projectId)
        .execute()
        .path("Users.search")
        .entityList(UserGql.class);
  }

  /** Returns list of timeentries from requested project between 'from' and 'to' dates. */
  public Entity<ServicesSearchResultGql, ?> searchActions(UUID projectId, LocalDate from, LocalDate to) {
    return tester.documentName("searchActions")
        .variable("projectId", projectId)
        .variable("from", from.toString())
        .variable("to", to.toString())
        .execute()
        .path("Actions.search")
        .entity(ServicesSearchResultGql.class);
  }
}
