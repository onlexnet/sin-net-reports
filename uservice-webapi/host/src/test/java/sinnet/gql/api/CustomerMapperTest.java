package sinnet.gql.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.time.LocalDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import sinnet.gql.models.EntityGql;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerMapper Tests")
class CustomerMapperTest {

  private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

  @Nested
  @DisplayName("gRPC LocalDateTime to Java LocalDateTime Mapping")
  class GrpcLocalDateTimeTests {

    @Test
    @DisplayName("should map gRPC LocalDateTime to java LocalDateTime")
    void shouldMapGrpcLocalDateTime() {
      var source = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .setHour(14)
          .setMinute(30)
          .setSecond(45)
          .build();

      var result = mapper.map(source);

      assertThat(result).isNotNull();
      assertThat(result).isEqualTo(LocalDateTime.of(2024, 12, 25, 14, 30, 45));
    }

    @Test
    @DisplayName("should handle midnight")
    void shouldHandleMidnight() {
      var source = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2025)
          .setMonth(1)
          .setDay(1)
          .setHour(0)
          .setMinute(0)
          .setSecond(0)
          .build();

      var result = mapper.map(source);

      assertThat(result.getHour()).isZero();
      assertThat(result.getMinute()).isZero();
      assertThat(result.getSecond()).isZero();
    }

    @Test
    @DisplayName("should handle end of day")
    void shouldHandleEndOfDay() {
      var source = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2025)
          .setMonth(12)
          .setDay(31)
          .setHour(23)
          .setMinute(59)
          .setSecond(59)
          .build();

      var result = mapper.map(source);

      assertThat(result).isEqualTo(LocalDateTime.of(2025, 12, 31, 23, 59, 59));
    }

    @Test
    @DisplayName("should handle partial seconds")
    void shouldHandlePartialSeconds() {
      var source = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(6)
          .setDay(15)
          .setHour(12)
          .setMinute(30)
          .setSecond(0)
          .build();

      var result = mapper.map(source);

      assertThat(result.getSecond()).isZero();
    }

    @Test
    @DisplayName("should return null for null input")
    void shouldReturnNullForNull() {
      assertThat(mapper.map((sinnet.grpc.customers.LocalDateTime) null)).isNull();
    }
  }

  @Nested
  @DisplayName("EntityGql to gRPC EntityId Mapping")
  class EntityGqlToGrpcEntityIdTests {

    @Test
    @DisplayName("should map EntityGql to gRPC EntityId")
    void shouldMapEntityGql() {
      var source = Instancio.of(EntityGql.class)
          .set(field(EntityGql::getProjectId), "proj-123")
          .set(field(EntityGql::getEntityId), "ent-456")
          .set(field(EntityGql::getEntityVersion), 42L)
          .create();

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo("proj-123");
      assertThat(result.getEntityId()).isEqualTo("ent-456");
      assertThat(result.getEntityVersion()).isEqualTo(42L);
    }

    @Test
    @DisplayName("should handle partial EntityGql")
    void shouldHandlePartialEntityGql() {
      var source = Instancio.of(EntityGql.class)
          .set(field(EntityGql::getEntityId), "minimal-id")
          .set(field(EntityGql::getEntityVersion), 1L)
          .create();

      var result = mapper.toGrpc(source);

      assertThat(result.getEntityId()).isEqualTo("minimal-id");
      assertThat(result.getEntityVersion()).isEqualTo(1L);
    }

    @Test
    @DisplayName("should handle different version numbers")
    void shouldHandleDifferentVersions() {
      var versions = new long[] {0L, 1L, 100L, Long.MAX_VALUE};

      for (var version : versions) {
        var source = Instancio.of(EntityGql.class)
            .set(field(EntityGql::getEntityId), "test-entity")
            .set(field(EntityGql::getEntityVersion), version)
            .create();

        var result = mapper.toGrpc(source);
        assertThat(result.getEntityVersion()).isEqualTo(version);
      }
    }

  }

  @Nested
  @DisplayName("Java LocalDateTime to gRPC LocalDateTime Mapping")
  class JavaLocalDateTimeToGrpcTests {

    @Test
    @DisplayName("should map java LocalDateTime to gRPC LocalDateTime")
    void shouldMapJavaLocalDateTime() {
      var source = LocalDateTime.of(2024, 12, 25, 14, 30, 45);

      var result = mapper.toGrpc(source);

      assertThat(result).isNotNull();
      assertThat(result.getYear()).isEqualTo(2024);
      assertThat(result.getMonth()).isEqualTo(12);
      assertThat(result.getDay()).isEqualTo(25);
      assertThat(result.getHour()).isEqualTo(14);
      assertThat(result.getMinute()).isEqualTo(30);
      assertThat(result.getSecond()).isEqualTo(45);
    }

    @Test
    @DisplayName("should handle midnight")
    void shouldHandleMidnight() {
      var source = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

      var result = mapper.toGrpc(source);

      assertThat(result.getHour()).isZero();
      assertThat(result.getMinute()).isZero();
      assertThat(result.getSecond()).isZero();
    }

    @Test
    @DisplayName("should handle end of day")
    void shouldHandleEndOfDay() {
      var source = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

      var result = mapper.toGrpc(source);

      assertThat(result.getHour()).isEqualTo(23);
      assertThat(result.getMinute()).isEqualTo(59);
      assertThat(result.getSecond()).isEqualTo(59);
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
        assertThat(result.getSecond()).isEqualTo(dateTime.getSecond());
      }
    }

    @Test
    @DisplayName("should return null for null LocalDateTime")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGrpc((LocalDateTime) null)).isNull();
    }
  }

  @Nested
  @DisplayName("gRPC EntityId to SomeEntityGql Mapping")
  class GrpcEntityIdToSomeEntityGqlTests {

    @Test
    @DisplayName("should map gRPC EntityId to SomeEntityGql")
    void shouldMapGrpcEntityId() {
      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId("proj-123")
          .setEntityId("ent-456")
          .setEntityVersion(42L)
          .build();

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo("proj-123");
      assertThat(result.getEntityId()).isEqualTo("ent-456");
      assertThat(result.getEntityVersion()).isEqualTo(42L);
    }

    @Test
    @DisplayName("should handle empty strings")
    void shouldHandleEmptyStrings() {
      var source = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId("")
          .setEntityId("")
          .setEntityVersion(0L)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getProjectId()).isEmpty();
      assertThat(result.getEntityId()).isEmpty();
    }

    @Test
    @DisplayName("should return null for null EntityId")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.common.EntityId) null)).isNull();
    }
  }

  @Nested
  @DisplayName("gRPC CustomerContact Mapping")
  class CustomerContactMappingTests {

    @Test
    @DisplayName("should map complete CustomerContact")
    void shouldMapCompleteCustomerContact() {
      var source = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Jane")
          .setLastName("Smith")
          .setPhoneNo("+48987654321")
          .setEmail("jane@example.com")
          .build();

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getFirstName()).isEqualTo("Jane");
      assertThat(result.getLastName()).isEqualTo("Smith");
      assertThat(result.getPhoneNo()).isEqualTo("+48987654321");
      assertThat(result.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("should handle partial CustomerContact")
    void shouldHandlePartialCustomerContact() {
      var source = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Jane")
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getFirstName()).isEqualTo("Jane");
      assertThat(result.getLastName()).isEmpty();
      assertThat(result.getPhoneNo()).isEmpty();
      assertThat(result.getEmail()).isEmpty();
    }

    @Test
    @DisplayName("should handle empty fields")
    void shouldHandleEmptyFields() {
      var source = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("")
          .setLastName("")
          .setPhoneNo("")
          .setEmail("")
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getFirstName()).isEmpty();
      assertThat(result.getLastName()).isEmpty();
    }

    @Test
    @DisplayName("should return null for null CustomerContact")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.customers.CustomerContact) null)).isNull();
    }
  }

  @Nested
  @DisplayName("gRPC CustomerSecretEx Mapping")
  class CustomerSecretExMappingTests {

    @Test
    @DisplayName("should map complete CustomerSecretEx with datetime formatting")
    void shouldMapCompleteCustomerSecretEx() {
      var changedWhen = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .setHour(14)
          .setMinute(30)
          .setSecond(45)
          .build();

      var source = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("HQ")
          .setUsername("admin")
          .setPassword("secret123")
          .setEntityCode("CODE_001")
          .setEntityName("Main Entity")
          .setChangedWhen(changedWhen)
          .setChangedWho("john.doe@example.com")
          .setOtpSecret("otp-secret")
          .setOtpRecoveryKeys("key1,key2,key3")
          .build();

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getLocation()).isEqualTo("HQ");
      assertThat(result.getUsername()).isEqualTo("admin");
      assertThat(result.getPassword()).isEqualTo("secret123");
      assertThat(result.getEntityCode()).isEqualTo("CODE_001");
      assertThat(result.getEntityName()).isEqualTo("Main Entity");
      assertThat(result.getChangedWho()).isEqualTo("john.doe@example.com");
      assertThat(result.getOtpSecret()).isEqualTo("otp-secret");
      assertThat(result.getOtpRecoveryKeys()).isEqualTo("key1,key2,key3");
      assertThat(result.getChangedWhen()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}");
    }

    @Test
    @DisplayName("should handle partial CustomerSecretEx with valid datetime")
    void shouldHandlePartialCustomerSecretEx() {
      var changedWhen = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(1)
          .setDay(1)
          .setHour(0)
          .setMinute(0)
          .setSecond(0)
          .build();

      var source = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("Minimal")
          .setUsername("user")
          .setChangedWhen(changedWhen)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getLocation()).isEqualTo("Minimal");
      assertThat(result.getUsername()).isEqualTo("user");
      assertThat(result.getPassword()).isEmpty();
    }

    @Test
    @DisplayName("should handle missing changedWhen with default value")
    void shouldHandleMissingChangedWhen() {
      var source = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("Test")
          .setUsername("test-user")
          .setPassword("pass")
          .build();

      // Note: When changedWhen is not set, the mapper will use "?" as default
      // This requires the mapper implementation to handle null/missing changedWhen
      var result = mapper.toGql(source);

      assertThat(result.getChangedWhen()).isEqualTo("?");
    }

    @Test
    @DisplayName("should format ISO_DATE_TIME correctly")
    void shouldFormatIsoDateTime() {
      var changedWhen = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(1)
          .setDay(15)
          .setHour(9)
          .setMinute(30)
          .setSecond(0)
          .build();

      var source = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("L")
          .setUsername("u")
          .setPassword("p")
          .setChangedWhen(changedWhen)
          .build();

      var result = mapper.toGql(source);

      var expectedDateTime = LocalDateTime.of(2024, 1, 15, 9, 30, 0);
      var expectedFormatted = CustomerMapper.TIMESTAMP_FORMATTER.format(expectedDateTime);
      assertThat(result.getChangedWhen()).isEqualTo(expectedFormatted);
    }

    @Test
    @DisplayName("should return null for null CustomerSecretEx")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.customers.CustomerSecretEx) null)).isNull();
    }
  }

  @Nested
  @DisplayName("gRPC CustomerSecret Mapping")
  class CustomerSecretMappingTests {

    @Test
    @DisplayName("should map complete CustomerSecret")
    void shouldMapCompleteCustomerSecret() {
      var changedWhen = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .setHour(14)
          .setMinute(30)
          .setSecond(45)
          .build();

      var source = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Office")
          .setUsername("user123")
          .setPassword("pass456")
          .setChangedWhen(changedWhen)
          .setChangedWho("editor@example.com")
          .setOtpSecret("secret")
          .setOtpRecoveryKeys("keys")
          .build();

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getLocation()).isEqualTo("Office");
      assertThat(result.getUsername()).isEqualTo("user123");
      assertThat(result.getPassword()).isEqualTo("pass456");
      assertThat(result.getChangedWho()).isEqualTo("editor@example.com");
      assertThat(result.getOtpSecret()).isEqualTo("secret");
      assertThat(result.getOtpRecoveryKeys()).isEqualTo("keys");
      assertThat(result.getChangedWhen()).isNotEmpty();
    }

    @Test
    @DisplayName("should handle partial CustomerSecret with valid datetime")
    void shouldHandlePartialCustomerSecret() {
      var changedWhen = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(1)
          .setDay(1)
          .setHour(0)
          .setMinute(0)
          .setSecond(0)
          .build();

      var source = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Simple")
          .setUsername("simple-user")
          .setChangedWhen(changedWhen)
          .build();

      var result = mapper.toGql(source);

      assertThat(result.getLocation()).isEqualTo("Simple");
      assertThat(result.getUsername()).isEqualTo("simple-user");
      assertThat(result.getPassword()).isEmpty();
    }

    @Test
    @DisplayName("should handle missing changedWhen with default value")
    void shouldHandleMissingChangedWhen() {
      var source = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Test")
          .setUsername("test")
          .setPassword("pass123")
          .build();

      // Note: When changedWhen is not set, the mapper should use "?" as default
      // This requires the mapper implementation to handle null/missing changedWhen
      var result = mapper.toGql(source);

      assertThat(result.getChangedWhen()).isEqualTo("?");
    }

    @Test
    @DisplayName("should return null for null CustomerSecret")
    void shouldReturnNullForNull() {
      assertThat(mapper.toGql((sinnet.grpc.customers.CustomerSecret) null)).isNull();
    }
  }

  @Nested
  @DisplayName("Round-trip Mappings")
  class RoundTripTests {

    @Test
    @DisplayName("should round-trip LocalDateTime with seconds precision")
    void shouldRoundTripLocalDateTime() {
      // gRPC LocalDateTime only supports seconds precision, not nanoseconds
      var original = LocalDateTime.of(2024, 12, 25, 14, 30, 45);

      var toGrpc = mapper.toGrpc(original);
      var backToJava = mapper.map(toGrpc);

      assertThat(backToJava).isEqualTo(original);
    }

    @Test
    @DisplayName("should handle multiple round-trips with random data")
    void shouldHandleMultipleRoundTrips() {
      // Create random date-times but with seconds precision only (no nanos)
      var randomDateTimes = Instancio.ofList(LocalDateTime.class)
          .size(5)
          .create()
          .stream()
          .map(dt -> dt.withNano(0))  // Strip nanoseconds for gRPC compatibility
          .toList();

      for (var dateTime : randomDateTimes) {
        var grpc = mapper.toGrpc(dateTime);
        var result = mapper.map(grpc);
        assertThat(result).isEqualTo(dateTime);
      }
    }

    @Test
    @DisplayName("should handle complex workflow with random EntityGql")
    void shouldHandleComplexWorkflow() {
      var randomEntities = Instancio.ofList(EntityGql.class).size(3).create();

      for (var entity : randomEntities) {
        var grpc = mapper.toGrpc(entity);
        assertThat(grpc).isNotNull();
        assertThat(grpc.getEntityId()).isEqualTo(entity.getEntityId());
        assertThat(grpc.getEntityVersion()).isEqualTo(entity.getEntityVersion());
      }
    }
  }
}
