package onlexnet.sinnet.webapi.test;

import java.time.Duration;
import java.util.List;

import org.springframework.graphql.test.tester.GraphQlTester.Entity;
import org.springframework.graphql.test.tester.GraphQlTester.EntityList;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.client.HttpClient;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.SomeEntityGql;
import sinnet.gql.models.UserGql;

/** Set of methods used to test application functionlity. */
public class AppApi {

  private final HttpGraphQlTester tester;

  /** TBD. */
  @SneakyThrows
  public AppApi(String rootUri, String email) {
    var token = Jwt.createTestJwt(email);

    var http11SslContextSpec = Http11SslContextSpec
        .forClient()
        .configure(builder -> builder.trustManager(InsecureTrustManagerFactory.INSTANCE));

    // create a custom connector and customize TLS configuration there.
    // Ustawienie niestandardowego SSLContext w WebTestClient
    var httpClient = HttpClient.create()
        .secure(spec -> {
          spec.sslContext(http11SslContextSpec);
        });
    var connector = new ReactorClientHttpConnector(httpClient);

    var client = WebTestClient.bindToServer(connector)
        .responseTimeout(Duration.ofMinutes(10))
        .baseUrl(rootUri + "/graphql")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
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
}
