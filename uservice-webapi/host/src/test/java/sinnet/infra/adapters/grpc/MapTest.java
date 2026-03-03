package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import sinnet.domain.models.UserToken;

class MapTest {

  @Test
  void shouldMapUserTokenToGrpc() {
    var projectId = UUID.randomUUID();
    var source = new UserToken(projectId, "requestor@example.com");

    var result = Map.apply.map(source);

    assertThat(result).isNotNull();
    assertThat(result.getProjectId()).isEqualTo(projectId.toString());
    assertThat(result.getRequestorEmail()).isEqualTo("requestor@example.com");
  }

  @Test
  void shouldReturnNullForNullSource() {
    assertThat(Map.apply.map(null)).isNull();
  }
}