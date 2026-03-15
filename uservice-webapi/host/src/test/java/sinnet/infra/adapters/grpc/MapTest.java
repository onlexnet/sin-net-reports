package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.UserToken;
import sinnet.infra.adapters.grpc.Map;

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
  void shouldReturnNullForNullUserToken() {
    assertThat(Map.apply.map((UserToken) null)).isNull();
  }

  @Test
  void shouldMapEntityIdToGrpc() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var source = new EntityId(projectId, entityId, 7L);

    var result = Map.apply.map(source);

    assertThat(result).isNotNull();
    assertThat(result.getProjectId()).isEqualTo(projectId.toString());
    assertThat(result.getEntityId()).isEqualTo(entityId.toString());
    assertThat(result.getEntityVersion()).isEqualTo(7L);
  }

  @Test
  void shouldReturnNullForNullEntityId() {
    assertThat(Map.apply.map((EntityId) null)).isNull();
  }
}