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

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PostgresDbExtension.class)
class UserApiTest {
  
  @Value("${grpc.server.port}")
  private int grpcPort;

  @Test
  public void readEmptyUsers() {

    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();
    var token = UserToken.newBuilder().setProjectId(UUID.randomUUID().toString()).build();
    var service = UsersGrpc.newBlockingStub(channel);
    var request = SearchRequest.newBuilder().setUserToken(token) .build();
    
    var actual = service.search(request);
    
    var expected = SearchReply.newBuilder().build();

    Assertions.assertThat(actual).isEqualTo(expected);
  }
}
