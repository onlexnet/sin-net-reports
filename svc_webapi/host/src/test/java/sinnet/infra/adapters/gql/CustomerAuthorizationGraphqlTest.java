package sinnet.infra.adapters.gql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.graphql.data.method.annotation.support.AnnotatedControllerConfigurer;
import org.springframework.graphql.execution.GraphQlSource;

import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.TypeRuntimeWiring;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.flow.request.CustomerUpdateResult;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.domain.models.UserToken;

class CustomerAuthorizationGraphqlTest {

  private final GenericApplicationContext context = new GenericApplicationContext();
  private final AtomicReference<CustomerUpdateCommand> saved = new AtomicReference<>();
  private GraphQL graphQl;

  @BeforeEach
  void configureControllers() {
    var service = mock(CustomersPortIn.class);
    var projectId = UUID.randomUUID();
    var user = new UserToken(projectId, "operator@example.test");
    when(service.update(any())).thenAnswer(invocation -> {
      var command = invocation.getArgument(0, CustomerUpdateCommand.class);
      saved.set(command);
      return new CustomerUpdateResult(command.id());
    });
    when(service.get(any())).thenAnswer(invocation ->
        new CustomerGetResult(saved.get().id(), saved.get().value()));
    context.registerBean(CustomersMutationSave.class, () -> new CustomersMutationSave(
        () -> LocalDateTime.of(2020, 1, 2, 3, 4), service));
    context.registerBean(CustomersQueryGet.class, () -> new CustomersQueryGet(service));
    context.refresh();
    var controllers = new AnnotatedControllerConfigurer();
    controllers.setApplicationContext(context);
    controllers.afterPropertiesSet();
    graphQl = GraphQlSource.schemaResourceBuilder()
        .schemaResources(new ClassPathResource("graphql/schema.graphqls"))
        .configureRuntimeWiring(controllers)
        .configureRuntimeWiring(builder -> builder
            .scalar(ExtendedScalars.GraphQLLong)
            .scalar(ExtendedScalars.Date)
            .type(TypeRuntimeWiring.newTypeWiring("Mutation").dataFetcher("Customers",
                environment -> new CustomersMutation(projectId, user)))
            .type(TypeRuntimeWiring.newTypeWiring("Query").dataFetcher("Customers",
                environment -> new CustomersQuery(user))))
        .build().graphQl();
  }

  @AfterEach
  void closeContext() {
    context.close();
  }

  @Test
  void shouldSaveAndReadBothFormatsInBothGroups() {
    var credentials = """
        [{location: "Portal", username: "user", password: "pass", otpSecret: "seed",
          otpRecoveryKeys: "recovery", authorizationGroup: SINNET},
         {location: "Portal", username: "user", password: "pass", otpSecret: "seed",
          otpRecoveryKeys: "recovery", authorizationGroup: CUSTOMER}]
        """;
    assertThat(graphQl.execute(mutation(credentials, credentials)).getErrors()).isEmpty();
    var result = graphQl.execute("""
        { Customers(projectId: "project") { get(entityId: "customer") {
          secrets { authorizationGroup username password otpSecret otpRecoveryKeys }
          secretsEx { authorizationGroup username password otpSecret otpRecoveryKeys }
        } } }
        """);
    var expectedCredentials = List.of(
        Map.of("authorizationGroup", "SINNET", "username", "user", "password", "pass",
            "otpSecret", "seed", "otpRecoveryKeys", "recovery"),
        Map.of("authorizationGroup", "CUSTOMER", "username", "user", "password", "pass",
            "otpSecret", "seed", "otpRecoveryKeys", "recovery"));
    assertThat(result.toSpecification()).isEqualTo(Map.of("data",
        Map.of("Customers", Map.of("get", Map.of(
            "secrets", expectedCredentials, "secretsEx", expectedCredentials)))));
  }

  @Test
  void shouldDefaultOmittedGroupsToSinnet() {
    var result = graphQl.execute(mutation("[{location: \"Portal\"}]", "[{location: \"Portal\"}]"));
    assertThat(result.getErrors()).isEmpty();
    assertThat(List.of(saved.get().value().secrets().getFirst().authorizationGroup(),
        saved.get().value().secretsEx().getFirst().authorizationGroup()))
        .containsOnly(sinnet.domain.models.AuthorizationGroup.SINNET);
  }

  @Test
  void shouldRejectExplicitNullAndUnknownGroupsWithoutSaving() {
    for (var group : List.of("null", "INVALID")) {
      for (var extended : List.of(false, true)) {
        var credentials = "[{location: \"Portal\", authorizationGroup: " + group + "}]";
        var result = graphQl.execute(mutation(
            extended ? "[]" : credentials, extended ? credentials : "[]"));
        assertThat(result.getErrors()).isNotEmpty();
      }
    }
    assertThat(saved.get()).isNull();
  }

  private String mutation(String secrets, String secretsEx) {
    return """
        mutation { Customers(projectId: "project") {
          save(id: {projectId: "00000000-0000-0000-0001-000000000001",
                    entityId: "00000000-0000-0000-0009-000000000001", entityVersion: 0},
               entry: {customerName: "Customer"}, secrets: %s, secretsEx: %s, contacts: []) {
            entityId
          }
        } }
        """.formatted(secrets, secretsEx);
  }
}
