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

  Context ctx() {
    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();
    var token = UserToken.newBuilder().setProjectId(UUID.randomUUID().toString()).build();
    var service = UsersGrpc.newBlockingStub(channel);
    return new Context(service, token);
  }

  @Test
  void readEmptyUsers() {
    var ctx = ctx();
    var request = SearchRequest.newBuilder().setUserToken(ctx.utoken()).build();
    var actual = ctx.rpc().search(request);

    var expected = SearchReply.newBuilder().build();

    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void readEmptyUsers1() {
    var ctx = ctx();
    var request = SearchRequest.newBuilder()
      .setUserToken(ctx.utoken())
      .build();
    var actual = ctx.rpc().search(request);

    var expected = SearchReply.newBuilder().build();

    Assertions.assertThat(actual).isEqualTo(expected);
  }

}
