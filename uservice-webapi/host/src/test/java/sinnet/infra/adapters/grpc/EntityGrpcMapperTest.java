package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sinnet.domain.models.EntityId;

@DisplayName("EntityGrpcMapper Tests")
class EntityGrpcMapperTest {

  private final EntityGrpcMapper mapper = EntityGrpcMapper.INSTANCE;

  @Test
  @DisplayName("should map domain EntityId to grpc EntityId")
  void shouldMapDomainToGrpc() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var source = new EntityId(projectId, entityId, 12L);

    var result = mapper.toGrpc(source);

    assertThat(result.getProjectId()).isEqualTo(projectId.toString());
    assertThat(result.getEntityId()).isEqualTo(entityId.toString());
    assertThat(result.getEntityVersion()).isEqualTo(12L);
  }

  @Test
  @DisplayName("should map grpc EntityId to domain EntityId")
  void shouldMapGrpcToDomain() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();
    var source = sinnet.grpc.common.EntityId.newBuilder()
        .setProjectId(projectId.toString())
        .setEntityId(entityId.toString())
        .setEntityVersion(33L)
        .build();

    var result = mapper.fromGrpc(source);

    assertThat(result).isEqualTo(new EntityId(projectId, entityId, 33L));
  }

  @Test
  @DisplayName("should map empty grpc ids to null domain UUIDs")
  void shouldMapEmptyStringsToNullUuids() {
    var source = sinnet.grpc.common.EntityId.newBuilder()
        .setEntityVersion(1L)
        .build();

    var result = mapper.fromGrpc(source);

    assertThat(result).isEqualTo(new EntityId(null, null, 1L));
  }

  @Test
  @DisplayName("should map LocalDate to grpc LocalDate")
  void shouldMapLocalDateToGrpc() {
    var source = LocalDate.of(2025, 1, 7);

    var result = mapper.toGrpc(source);

    assertThat(result.getYear()).isEqualTo(2025);
    assertThat(result.getMonth()).isEqualTo(1);
    assertThat(result.getDay()).isEqualTo(7);
  }

  @Test
  @DisplayName("should map grpc LocalDate to LocalDate")
  void shouldMapGrpcLocalDateToLocalDate() {
    var source = sinnet.grpc.timeentries.LocalDate.newBuilder()
        .setYear(2026)
        .setMonth(3)
        .setDay(11)
        .build();

    var result = mapper.fromGrpc(source);

    assertThat(result).isEqualTo(LocalDate.of(2026, 3, 11));
  }

  @Test
  @DisplayName("should map LocalDateTime to grpc LocalDateTime")
  void shouldMapLocalDateTimeToGrpc() {
    var source = LocalDateTime.of(2026, 3, 11, 10, 15, 20);

    var result = mapper.toGrpc(source);

    assertThat(result.getYear()).isEqualTo(2026);
    assertThat(result.getMonth()).isEqualTo(3);
    assertThat(result.getDay()).isEqualTo(11);
    assertThat(result.getHour()).isEqualTo(10);
    assertThat(result.getMinute()).isEqualTo(15);
  }
}
