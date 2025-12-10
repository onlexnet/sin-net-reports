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
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.FileDownloadResultGql;
import sinnet.gql.models.ProjectEntityGql;
import sinnet.gql.models.ServiceModelGql;
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

  /**
   * Invokes Customers.reserve
   */
  public Entity<SomeEntityGql, ?> saveCustomer(UUID projectId, 
                                               SomeEntityGql id,
                                               CustomerInput entry,
                                               List<CustomerSecretInput> secrets,
                                               List<CustomerSecretExInput> secretsEx,
                                               List<CustomerContactInputGql> contacts) {
    return tester.documentName("saveCustomer")
        .variable("projectId", projectId.toString())
        .variable("id", id)
        .variable("entry", entry)
        .variable("secrets", secrets)
        .variable("secretsEx", secretsEx)
        .variable("contacts", contacts)
        .execute()
        .path("Customers.save")
        .entity(SomeEntityGql.class);
  }

  /**
   * Invokes Customers.reserve
   */
  public Entity<List<CustomerEntityGql>, ?> listCustomers(UUID projectId) {
    return tester.documentName("listCustomer")
        .variable("projectId", projectId.toString())
        .execute()
        .path("Customers.list")
        .entityList(CustomerEntityGql.class);
  }

  /**
   * Invokes Customers.reserve
   */
  public Entity<CustomerEntityGql, ?> getCustomer(UUID projectId, UUID customerId) {
    return tester.documentName("getCustomer")
        .variable("projectId", projectId.toString())
        .variable("entityId", customerId.toString())
        .execute()
        .path("Customers.get")
        .entity(CustomerEntityGql.class);
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

  /** Creates new action (placeholder). */
  public Entity<SomeEntityGql, ?> newAction(String projectId, LocalDate when) {
    return tester.documentName("newAction")
        .variable("projectId", projectId)
        .variable("when", when.toString())
        .execute()
        .path("Actions.newAction")
        .entity(SomeEntityGql.class);
  }

  /** Creates new action (placeholder). */
  public Entity<ServiceModelGql, ?> getAction(String projectId, String actionId) {
    return tester.documentName("getAction")
        .variable("projectId", projectId.toString())
        .variable("actionId", actionId.toString())
        .execute()
        .path("Actions.get")
        .entity(ServiceModelGql.class);
  }

  /** Downloads Actions list as Excel. */
  public Entity<FileDownloadResultGql, ?> downloadFile(String projectId, int year, int month) {
    return tester.documentName("downloadFile")
        .variable("projectId", projectId.toString())
        .variable("year", year)
        .variable("month", month)
        .execute()
        .path("downloadFile")
        .entity(FileDownloadResultGql.class);
  }

}
