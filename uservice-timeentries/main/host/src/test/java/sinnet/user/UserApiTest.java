package sinnet.user;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import io.grpc.ManagedChannelBuilder;
import sinnet.db.PostgresDbExtension;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersGrpc;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PostgresDbExtension.class)
class UserApiTest {

  @Value("${grpc.server.port}")
  private int grpcPort;

  record Context(UsersBlockingStub rpc, UserToken utoken) {
  }

  Context ctx(UUID projectId, String requestorEmail) {
    var projectIdAsString = projectId.toString();
    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();
    var token = UserToken.newBuilder()
        .setProjectId(projectIdAsString)
        .setRequestorEmail(requestorEmail)
        .build();
    var service = UsersGrpc.newBlockingStub(channel);
    return new Context(service, token);
  }

  Context ctx() {
    var userEmail = UUID.randomUUID() + "@example.of";
    var projectId = UUID.randomUUID();
    return ctx(projectId, userEmail);
  }

  @Test
  void readEmptyUsers() {
    var ctx = ctx();
    var request = SearchRequest.newBuilder()
        .setUserToken(ctx.utoken()).build();
    var actual = ctx.rpc().search(request);

    var expected = SearchReply.newBuilder().build();

    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void readPredefinedUsers() {
    var legalProject = UUID.fromString("00000000-0000-0000-0001-000000000001");
    var knownUser = "siudeks@gmail.com";
    var ctx = ctx(legalProject, knownUser);
    var request = SearchRequest.newBuilder()
        .setUserToken(ctx.utoken())
        .build();
    var actual = ctx.rpc().search(request);
    var actualUsers = actual.getItemsList().stream().map(it -> it.getEmail()).toList();

    Assertions.assertThat(actualUsers).containsOnly(knownUser, "user1@project1", "user2@project1");
  }

}
