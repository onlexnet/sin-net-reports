package sinnet;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;

import sinnet.gql.models.ProjectEntityGql;
import sinnet.grpc.ProjectsGrpcService;
import sinnet.grpc.projects.generated.ListReply;

@SpringBootTest(webEnvironment = RANDOM_PORT)
// @ActiveProfiles(Profiles.TEST)
@AutoConfigureMockMvc
class GqlTest {

  @Autowired
  TestRestTemplate restTemplate;

  @Autowired
  WebApplicationContext applicationContext;

  @MockBean
  NameTestCallback nameProvider;

  @MockBean
  ProjectsGrpcService projectsGrpcService;

  @Test
  void doSomething() {

    var grpcResult = ListReply.newBuilder()
        .addProjects(sinnet.grpc.projects.generated.Project.newBuilder()
          .build())
        .build();
    Mockito
      .when(projectsGrpcService.list(any()))
      .thenReturn(grpcResult);

    Mockito.when(nameProvider.getName()).thenReturn("my name");
    var rootUri = restTemplate.getRootUri();

    var client = WebTestClient.bindToServer()
        .responseTimeout(Duration.ofMinutes(10))
        .baseUrl(rootUri + "/graphql")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer token")
        .build();

    var tester = HttpGraphQlTester.create(client);

    var actual = tester.documentName("namedProjects")
        .variable("projectName", "spring-framework")
        .execute()
        .path("Projects.list")
        .entityList(ProjectEntityGql.class)
        .hasSizeGreaterThan(0);
  }

  // public static String createToken(String username) {
  // String jwt = Jwts.builder()
  // .setSubject(username)
  // .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
  // .signWith(SignatureAlgorithm.HS512, SECRET)
  // .compact();

  // return jwt;
  // }

  @TestConfiguration
  public static class TestSecurityConfig {

    static final String AUTH0_TOKEN = "token";
    static final String SUB = "sub";
    static final String AUTH0ID = "sms|12345678";

    @Autowired
    NameTestCallback nameProvider;

    @Bean
    public JwtDecoder jwtDecoder() {
      // This anonymous class needs for the possibility of using SpyBean in test
      // methods
      // Lambda cannot be a spy with spring @SpyBean annotation
      return new JwtDecoder() {
        @Override
        public Jwt decode(String token) {
          var name = nameProvider.getName();
          return jwt(name);
        }
      };
    }

    public Jwt jwt(String name) {

      // This is a place to add general and maybe custom claims which should be
      // available after parsing token in the live system
      var claims = Map.of(SUB, (Object) "aaaaaaaaaa", "emails", List.of("my email"));
  
      var headers = Map.of("alg", (Object) "none");
      // This is an object that represents contents of jwt token after parsing
      return new Jwt(
          AUTH0_TOKEN,
          Instant.now(),
          Instant.now().plusSeconds(30),
          headers,
          claims);
    }
  }

  interface NameTestCallback {
    String getName();
  }
}
