package sinnet.gql.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import sinnet.domain.EntityId;
import sinnet.gql.models.EntityGql;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommonMapper Tests")
class CommonMapperTest {

  private final CommonMapper mapper = Mappers.getMapper(CommonMapper.class);

  @Nested
  @DisplayName("ProjectId to EntityGql Mapping")
  class ProjectIdToEntityGqlTests {

    @Test
    @DisplayName("should map complete ProjectId to EntityGql")
    void shouldMapCompleteProjectId() {
      // Arrange
      var source = sinnet.grpc.projects.generated.ProjectId.newBuilder()
          .setEId("test-entity-id")
          .setETag(42L)
          .build();

      // Act
      var result = mapper.toGql(source);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo("test-entity-id");
      assertThat(result.getEntityId()).isEqualTo("test-entity-id");
      assertThat(result.getEntityVersion()).isEqualTo(42L);
    }

    @Test
    @DisplayName("should handle ProjectId with zero version")
    void shouldHandleZeroVersion() {
      var source = sinnet.grpc.projects.generated.ProjectId.newBuilder()
          .setEId("entity")
          .setETag(0L)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getEntityVersion()).isZero();
    }

    @Test
    @DisplayName("should handle ProjectId with large version number")
    void shouldHandleLargeVersion() {
      var source = sinnet.grpc.projects.generated.ProjectId.newBuilder()
          .setEId("entity")
          .setETag(Long.MAX_VALUE)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getEntityVersion()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("should return null for null ProjectId")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.projects.generated.ProjectId) null)).isNull();
    }
  }

  @Nested
  @DisplayName("gRPC EntityId to EntityGql Mapping")
  class GrpcEntityIdToEntityGqlTests {

    @Test
    @DisplayName("should map complete gRPC EntityId to EntityGql")
    void shouldMapCompleteGrpcEntityId() {
      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId("proj-123")
          .setEntityId("ent-456")
          .setEntityVersion(99L)
          .build();

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo("proj-123");
      assertThat(result.getEntityId()).isEqualTo("ent-456");
      assertThat(result.getEntityVersion()).isEqualTo(99L);
    }

    @Test
    @DisplayName("should handle empty string ids")
    void shouldHandleEmptyStrings() {
      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId("")
          .setEntityId("")
          .setEntityVersion(1L)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getProjectId()).isEmpty();
      assertThat(result.getEntityId()).isEmpty();
    }

    @Test
    @DisplayName("should return null for null gRPC EntityId")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.common.EntityId) null)).isNull();
    }
  }

  @Nested
  @DisplayName("EntityGql to ProjectId Mapping")
  class EntityGqlToProjectIdTests {

    @Test
    @DisplayName("should map EntityGql to ProjectId")
    void shouldMapEntityGqlToProjectId() {
      var source = Instancio.of(EntityGql.class)
          .set(field(EntityGql::getProjectId), "proj-xyz")
          .set(field(EntityGql::getEntityId), "ent-abc")
          .set(field(EntityGql::getEntityVersion), 123L)
          .create();

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getEId()).isEqualTo("ent-abc");
      assertThat(result.getETag()).isEqualTo(123L);
    }

    @Test
    @DisplayName("should handle partial EntityGql with minimal data")
    void shouldHandlePartialEntityGql() {
      var source = Instancio.of(EntityGql.class)
          .set(field(EntityGql::getEntityId), "minimal-id")
          .set(field(EntityGql::getEntityVersion), 1L)
          .create();

      var result = mapper.toGrpc(source);

      assertThat(result.getEId()).isEqualTo("minimal-id");
      assertThat(result.getETag()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should return null for null EntityGql")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGrpc((EntityGql) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Domain EntityId to gRPC EntityId Mapping")
  class DomainEntityIdToGrpcTests {

    @Test
    @DisplayName("should map domain EntityId to gRPC EntityId")
    void shouldMapDomainToGrpc() {
      var projectUuid = UUID.randomUUID();
      var entityUuid = UUID.randomUUID();
      var source = new EntityId(projectUuid, entityUuid, 77L);

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo(projectUuid.toString());
      assertThat(result.getEntityId()).isEqualTo(entityUuid.toString());
      assertThat(result.getEntityVersion()).isEqualTo(77L);
    }

    @Test
    @DisplayName("should handle nil UUIDs")
    void shouldHandleNilUuids() {
      var nilUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
      var source = new EntityId(nilUuid, nilUuid, 0L);

      var result = mapper.toGrpc(source);

      assertThat(result.getProjectId()).isEqualTo("00000000-0000-0000-0000-000000000000");
      assertThat(result.getEntityId()).isEqualTo("00000000-0000-0000-0000-000000000000");
    }

    @Test
    @DisplayName("should handle various UUID formats")
    void shouldHandleVariousUuids() {
      var testUuids = new UUID[] {
          UUID.randomUUID(),
          UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff"),
          UUID.nameUUIDFromBytes("test".getBytes())
      };

      for (var uuid : testUuids) {
        var source = new EntityId(uuid, UUID.randomUUID(), 1L);
        var result = mapper.toGrpc(source);
        assertThat(result.getProjectId()).isEqualTo(uuid.toString());
      }
    }

    @Test
    @DisplayName("should return null for null domain EntityId")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGrpc((EntityId) null)).isNull();
    }
  }

  @Nested
  @DisplayName("LocalDate Mapping")
  class LocalDateMappingTests {

    @Test
    @DisplayName("should map LocalDate to gRPC LocalDate")
    void shouldMapLocalDate() {
      var source = LocalDate.of(2024, 12, 25);

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getYear()).isEqualTo(2024);
      assertThat(result.getMonth()).isEqualTo(12);
      assertThat(result.getDay()).isEqualTo(25);
    }

    @Test
    @DisplayName("should handle leap day")
    void shouldHandleLeapDay() {
      var source = LocalDate.of(2024, 2, 29);

      var result = mapper.toGrpc(source);

      assertThat(result.getMonth()).isEqualTo(2);
      assertThat(result.getDay()).isEqualTo(29);
    }

    @Test
    @DisplayName("should handle first day of year")
    void shouldHandleFirstDayOfYear() {
      var source = LocalDate.of(2025, 1, 1);

      var result = mapper.toGrpc(source);

      assertThat(result.getYear()).isEqualTo(2025);
      assertThat(result.getMonth()).isEqualTo(1);
      assertThat(result.getDay()).isEqualTo(1);
    }

    @Test
    @DisplayName("should handle last day of year")
    void shouldHandleLastDayOfYear() {
      var source = LocalDate.of(2024, 12, 31);

      var result = mapper.toGrpc(source);

      assertThat(result.getMonth()).isEqualTo(12);
      assertThat(result.getDay()).isEqualTo(31);
    }

    @Test
    @DisplayName("should handle random dates generated by Instancio")
    void shouldHandleRandomDates() {
      var randomDates = Instancio.ofList(LocalDate.class).size(10).create();

      for (var date : randomDates) {
        var result = mapper.toGrpc(date);
        assertThat(result.getYear()).isEqualTo(date.getYear());
        assertThat(result.getMonth()).isEqualTo(date.getMonthValue());
        assertThat(result.getDay()).isEqualTo(date.getDayOfMonth());
      }
    }

    @Test
    @DisplayName("should return null for null LocalDate")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGrpc((LocalDate) null)).isNull();
    }
  }

  @Nested
  @DisplayName("LocalDateTime Mapping")
  class LocalDateTimeMappingTests {

    @Test
    @DisplayName("should map LocalDateTime to gRPC LocalDateTime")
    void shouldMapLocalDateTime() {
      var source = LocalDateTime.of(2024, 12, 25, 14, 30, 45);

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getYear()).isEqualTo(2024);
      assertThat(result.getMonth()).isEqualTo(12);
      assertThat(result.getDay()).isEqualTo(25);
      assertThat(result.getHour()).isEqualTo(14);
      assertThat(result.getMinute()).isEqualTo(30);
    }

    @Test
    @DisplayName("should handle midnight")
    void shouldHandleMidnight() {
      var source = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

      var result = mapper.toGrpc(source);

      assertThat(result.getHour()).isZero();
      assertThat(result.getMinute()).isZero();
    }

    @Test
    @DisplayName("should handle end of day")
    void shouldHandleEndOfDay() {
      var source = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

      var result = mapper.toGrpc(source);

      assertThat(result.getHour()).isEqualTo(23);
      assertThat(result.getMinute()).isEqualTo(59);
    }

    @Test
    @DisplayName("should handle random date-times generated by Instancio")
    void shouldHandleRandomDateTimes() {
      var randomDateTimes = Instancio.ofList(LocalDateTime.class).size(10).create();

      for (var dateTime : randomDateTimes) {
        var result = mapper.toGrpc(dateTime);
        assertThat(result.getYear()).isEqualTo(dateTime.getYear());
        assertThat(result.getMonth()).isEqualTo(dateTime.getMonthValue());
        assertThat(result.getDay()).isEqualTo(dateTime.getDayOfMonth());
        assertThat(result.getHour()).isEqualTo(dateTime.getHour());
        assertThat(result.getMinute()).isEqualTo(dateTime.getMinute());
      }
    }

    @Test
    @DisplayName("should return null for null LocalDateTime")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGrpc((LocalDateTime) null)).isNull();
    }
  }

  @Nested
  @DisplayName("fromGrpc EntityId Mapping")
  class FromGrpcEntityIdTests {

    @Test
    @DisplayName("should convert gRPC EntityId to domain EntityId")
    void shouldConvertGrpcToDomain() {
      var projectUuid = UUID.randomUUID();
      var entityUuid = UUID.randomUUID();

      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId(projectUuid.toString())
          .setEntityId(entityUuid.toString())
          .setEntityVersion(55L)
          .build();

      var result = mapper.fromGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.projectId()).isEqualTo(projectUuid);
      assertThat(result.id()).isEqualTo(entityUuid);
      assertThat(result.tag()).isEqualTo(55L);
    }

    @Test
    @DisplayName("should handle nil UUID")
    void shouldHandleNilUuid() {
      var nilUuid = "00000000-0000-0000-0000-000000000000";

      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId(nilUuid)
          .setEntityId(nilUuid)
          .setEntityVersion(0L)
          .build();

      var result = mapper.fromGrpc(source);

      assertThat(result.projectId()).isEqualTo(UUID.fromString(nilUuid));
      assertThat(result.id()).isEqualTo(UUID.fromString(nilUuid));
    }

    @Test
    @DisplayName("should return null for null gRPC EntityId")
    void shouldReturnNullForNull() {
      assertThat(mapper.fromGrpc((sinnet.grpc.common.EntityId) null)).isNull();
    }
  }

  @Nested
  @DisplayName("fromGrpc LocalDate Mapping")
  class FromGrpcLocalDateTests {

    @Test
    @DisplayName("should convert gRPC LocalDate to java LocalDate")
    void shouldConvertGrpcLocalDate() {
      var source = sinnet.grpc.timeentries.LocalDate.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .build();

      var result = mapper.fromGrpc(source);

      assertThat(result).isEqualTo(LocalDate.of(2024, 12, 25));
    }

    @Test
    @DisplayName("should handle leap day")
    void shouldHandleLeapDay() {
      var source = sinnet.grpc.timeentries.LocalDate.newBuilder()
          .setYear(2024)
          .setMonth(2)
          .setDay(29)
          .build();

      var result = mapper.fromGrpc(source);

      assertThat(result).isEqualTo(LocalDate.of(2024, 2, 29));
    }

    @Test
    @DisplayName("should return null for null gRPC LocalDate")
    void shouldReturnNullForNull() {
      assertThat(mapper.fromGrpc((sinnet.grpc.timeentries.LocalDate) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Round-trip Mappings")
  class RoundTripTests {

    @Test
    @DisplayName("should round-trip LocalDate")
    void shouldRoundTripLocalDate() {
      var original = Instancio.create(LocalDate.class);

      var grpc = mapper.toGrpc(original);
      var backToJava = mapper.fromGrpc(grpc);

      assertThat(backToJava).isEqualTo(original);
    }

    @Test
    @DisplayName("should round-trip EntityId through gRPC")
    void shouldRoundTripEntityId() {
      var original = new EntityId(UUID.randomUUID(), UUID.randomUUID(), 42L);

      var grpc = mapper.toGrpc(original);
      var backToDomain = mapper.fromGrpc(grpc);

      assertThat(backToDomain.projectId()).isEqualTo(original.projectId());
      assertThat(backToDomain.id()).isEqualTo(original.id());
      assertThat(backToDomain.tag()).isEqualTo(original.tag());
    }

    @Test
    @DisplayName("should handle multiple round-trips with random data")
    void shouldHandleMultipleRoundTrips() {
      var randomDates = Instancio.ofList(LocalDate.class).size(5).create();

      for (var date : randomDates) {
        var grpc = mapper.toGrpc(date);
        var result = mapper.fromGrpc(grpc);
        assertThat(result).isEqualTo(date);
      }
    }
  }
}
