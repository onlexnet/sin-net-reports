package sinnet.infra.adapters.gql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.time.LocalDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import sinnet.domain.models.Customer;
import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.EntityId;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;

@DisplayName("CustomerMapper (gql adapter) Tests")
class CustomerMapperTest {

  private final CustomerMapper mapper = CustomerMapper.INSTANCE;

  @Nested
  @DisplayName("gql -> domain mappings")
  class GqlToDomainTests {

    @Test
    @DisplayName("should map minimal CustomerInput to domain")
    void shouldMapMinimalCustomerInput() {
      var source = new CustomerInput();
      source.setCustomerName("Minimal Customer");

      var result = mapper.toDomain(source);

      assertThat(result).isEqualTo(new CustomerEntry(
          null,
          null,
          null,
          0,
          "Minimal Customer",
          null,
          null,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          null,
          false,
          null,
          null));
    }

    @Test
    @DisplayName("should map full CustomerInput to domain")
    void shouldMapFullCustomerInput() {
      var source = new CustomerInput();
      source.setOperatorEmail("operator@example.com");
      source.setBillingModel("subscription");
      source.setSupportStatus("active");
      source.setDistance(15);
      source.setCustomerName("ACME Clinic");
      source.setCustomerCityName("Warsaw");
      source.setCustomerAddress("Main St 1");
      source.setNfzUmowa(true);
      source.setNfzMaFilie(true);
      source.setNfzLekarz(true);
      source.setNfzPolozna(true);
      source.setNfzPielegniarkaSrodowiskowa(true);
      source.setNfzMedycynaSzkolna(true);
      source.setNfzTransportSanitarny(true);
      source.setNfzNocnaPomocLekarska(true);
      source.setNfzAmbulatoryjnaOpiekaSpecjalistyczna(true);
      source.setNfzRehabilitacja(true);
      source.setNfzStomatologia(true);
      source.setNfzPsychiatria(true);
      source.setNfzSzpitalnictwo(true);
      source.setNfzProgramyProfilaktyczne(true);
      source.setNfzZaopatrzenieOrtopedyczne(true);
      source.setNfzOpiekaDlugoterminowa(true);
      source.setNfzNotatki("NFZ notes");
      source.setKomercjaJest(true);
      source.setKomercjaNotatki("Commercial notes");
      source.setDaneTechniczne("Tech data");

      var result = mapper.toDomain(source);

      assertThat(result).isEqualTo(new CustomerEntry(
          "operator@example.com",
          "subscription",
          "active",
          15,
          "ACME Clinic",
          "Warsaw",
          "Main St 1",
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          "NFZ notes",
          true,
          "Commercial notes",
          "Tech data"));
    }

    @Test
    @DisplayName("should map minimal and full CustomerSecretInput to domain")
    void shouldMapCustomerSecretInput() {
      var minimal = new CustomerSecretInput().setLocation("HQ");
      var full = new CustomerSecretInput()
          .setLocation("Datacenter")
          .setUsername("ops")
          .setPassword("p@ss")
          .setOtpSecret("otp")
          .setOtpRecoveryKeys("k1,k2");

      assertThat(mapper.toDomain(minimal)).isEqualTo(new CustomerSecret("HQ", null, null, null, null));
      assertThat(mapper.toDomain(full)).isEqualTo(new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2"));
    }

    @Test
    @DisplayName("should map minimal and full CustomerSecretExInput to domain")
    void shouldMapCustomerSecretExInput() {
      var minimal = new CustomerSecretExInput().setLocation("Branch");
      var full = new CustomerSecretExInput()
          .setLocation("HQ")
          .setUsername("root")
          .setPassword("very-secret")
          .setEntityName("EHR")
          .setEntityCode("EHR-01")
          .setOtpSecret("otp-seed")
          .setOtpRecoveryKeys("r1,r2");

      assertThat(mapper.toDomain(minimal)).isEqualTo(new CustomerSecretEx("Branch", null, null, null, null, null, null));
      assertThat(mapper.toDomain(full)).isEqualTo(new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2"));
    }

    @Test
    @DisplayName("should map minimal and full CustomerContactInputGql to domain")
    void shouldMapCustomerContactInput() {
      var minimal = new CustomerContactInputGql();
      minimal.setFirstName("Ada");
      var full = new CustomerContactInputGql();
      full.setFirstName("Jan");
      full.setLastName("Kowalski");
      full.setPhoneNo("+48123123123");
      full.setEmail("jan.kowalski@example.com");

      assertThat(mapper.toDomain(minimal)).isEqualTo(new CustomerContact("Ada", null, null, null));
      assertThat(mapper.toDomain(full)).isEqualTo(new CustomerContact("Jan", "Kowalski", "+48123123123", "jan.kowalski@example.com"));
    }
  }

  @Nested
  @DisplayName("grpc -> gql mappings")
  class GrpcToGqlTests {

    @Test
    @DisplayName("should map grpc secret and secretEx with timestamp formatting")
    void shouldMapGrpcSecretsToGql() {
      var when = sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .setHour(14)
          .setMinute(30)
          .setSecond(45)
          .build();

      var secret = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Office")
          .setUsername("user")
          .setPassword("pass")
          .setChangedWhen(when)
          .setChangedWho("editor@example.com")
          .setOtpSecret("otp")
          .setOtpRecoveryKeys("k1")
          .build();

      var secretEx = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("HQ")
          .setUsername("admin")
          .setPassword("secret")
          .setEntityCode("E-1")
          .setEntityName("Main")
          .setChangedWhen(when)
          .setChangedWho("editor@example.com")
          .setOtpSecret("otp2")
          .setOtpRecoveryKeys("k2")
          .build();

      assertThat(mapper.toGql(secret).getChangedWhen()).isEqualTo(LocalDateTime.of(2024, 12, 25, 14, 30, 45).toString());
      assertThat(mapper.toGql(secretEx).getChangedWhen()).isEqualTo(LocalDateTime.of(2024, 12, 25, 14, 30, 45).toString());
    }

    @Test
    @DisplayName("should map grpc secret with missing changedWhen as question mark")
    void shouldMapGrpcSecretMissingDate() {
      var source = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Test")
          .setUsername("user")
          .setPassword("pass")
          .build();

      assertThat(mapper.toGql(source).getChangedWhen()).isEqualTo("?");
    }
  }










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

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

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

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

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

        var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);
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

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

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

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

      assertThat(result.getHour()).isZero();
      assertThat(result.getMinute()).isZero();
      assertThat(result.getSecond()).isZero();
    }

    @Test
    @DisplayName("should handle end of day")
    void shouldHandleEndOfDay() {
      var source = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

      assertThat(result.getHour()).isEqualTo(23);
      assertThat(result.getMinute()).isEqualTo(59);
      assertThat(result.getSecond()).isEqualTo(59);
    }

    @Test
    @DisplayName("should handle random date-times generated by Instancio")
    void shouldHandleRandomDateTimes() {
      var randomDateTimes = Instancio.ofList(LocalDateTime.class).size(10).create();

      for (var dateTime : randomDateTimes) {
        var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(dateTime);
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
      assertThat(sinnet.infra.adapters.grpc.CustomerMapper.toGrpc((LocalDateTime) null)).isNull();
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
      var expectedFormatted = sinnet.infra.adapters.gql.CustomerMapper.TIMESTAMP_FORMATTER.format(expectedDateTime);
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
  @DisplayName("Customer Entry Mapping")
  class CustomerEntryMappingTests {

    @Test
    @DisplayName("should map minimal gql CustomerInput to domain with false defaults")
    void shouldMapMinimalGqlToDomain() {
      var source = new CustomerInput();
      source.setCustomerName("Minimal Customer");

      var result = mapper.toDomain(source);

        var expected = new CustomerEntry(
          null,
          null,
          null,
          0,
          "Minimal Customer",
          null,
          null,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          null,
          false,
          null,
          null);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("should map full gql CustomerInput to domain")
    void shouldMapFullGqlToDomain() {
      var source = new CustomerInput();
      source.setOperatorEmail("operator@example.com");
      source.setBillingModel("subscription");
      source.setSupportStatus("active");
      source.setDistance(15);
      source.setCustomerName("ACME Clinic");
      source.setCustomerCityName("Warsaw");
      source.setCustomerAddress("Main St 1");
      source.setNfzUmowa(true);
      source.setNfzMaFilie(true);
      source.setNfzLekarz(true);
      source.setNfzPolozna(true);
      source.setNfzPielegniarkaSrodowiskowa(true);
      source.setNfzMedycynaSzkolna(true);
      source.setNfzTransportSanitarny(true);
      source.setNfzNocnaPomocLekarska(true);
      source.setNfzAmbulatoryjnaOpiekaSpecjalistyczna(true);
      source.setNfzRehabilitacja(true);
      source.setNfzStomatologia(true);
      source.setNfzPsychiatria(true);
      source.setNfzSzpitalnictwo(true);
      source.setNfzProgramyProfilaktyczne(true);
      source.setNfzZaopatrzenieOrtopedyczne(true);
      source.setNfzOpiekaDlugoterminowa(true);
      source.setNfzNotatki("NFZ notes");
      source.setKomercjaJest(true);
      source.setKomercjaNotatki("Commercial notes");
      source.setDaneTechniczne("Tech data");

      var result = mapper.toDomain(source);

      assertThat(result).isEqualTo(new CustomerEntry(
          "operator@example.com",
          "subscription",
          "active",
          15,
          "ACME Clinic",
          "Warsaw",
          "Main St 1",
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          "NFZ notes",
          true,
          "Commercial notes",
          "Tech data"));
    }

    @Test
    @DisplayName("should map minimal domain CustomerEntry to grpc CustomerValue")
    void shouldMapMinimalDomainToGrpc() {
      var source = new CustomerEntry(
          null,
          null,
          null,
          0,
          "Minimal Customer",
          null,
          null,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          null,
          false,
          null,
          null);

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

          var expected = sinnet.grpc.customers.CustomerValue.newBuilder()
            .setCustomerName("Minimal Customer")
            .build();

          assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("should map full domain CustomerEntry to grpc CustomerValue")
    void shouldMapFullDomainToGrpc() {
      var source = new CustomerEntry(
          "operator@example.com",
          "subscription",
          "active",
          15,
          "ACME Clinic",
          "Warsaw",
          "Main St 1",
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          true,
          "NFZ notes",
          true,
          "Commercial notes",
          "Tech data");

      var result = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(source);

          var expected = sinnet.grpc.customers.CustomerValue.newBuilder()
            .setOperatorEmail("operator@example.com")
            .setBillingModel("subscription")
            .setSupportStatus("active")
            .setDistance(15)
            .setCustomerName("ACME Clinic")
            .setCustomerCityName("Warsaw")
            .setCustomerAddress("Main St 1")
            .setNfzUmowa(true)
            .setNfzMaFilie(true)
            .setNfzLekarz(true)
            .setNfzPolozna(true)
            .setNfzPielegniarkaSrodowiskowa(true)
            .setNfzMedycynaSzkolna(true)
            .setNfzTransportSanitarny(true)
            .setNfzNocnaPomocLekarska(true)
            .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(true)
            .setNfzRehabilitacja(true)
            .setNfzStomatologia(true)
            .setNfzPsychiatria(true)
            .setNfzSzpitalnictwo(true)
            .setNfzProgramyProfilaktyczne(true)
            .setNfzZaopatrzenieOrtopedyczne(true)
            .setNfzOpiekaDlugoterminowa(true)
            .setNfzNotatki("NFZ notes")
            .setKomercjaJest(true)
            .setKomercjaNotatki("Commercial notes")
            .setDaneTechniczne("Tech data")
            .build();

          assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("should return null for null gql CustomerInput")
    void shouldReturnNullForNullGqlInput() {
      assertThat(mapper.toDomain((CustomerInput) null)).isNull();
    }

    @Test
    @DisplayName("should return null for null domain CustomerEntry")
    void shouldReturnNullForNullDomainInput() {
      assertThat(sinnet.infra.adapters.grpc.CustomerMapper.toGrpc((CustomerEntry) null)).isNull();
    }
  }

  @Nested
  @DisplayName("GQL <-> Domain <-> gRPC bridge mapping")
  class GqlDomainGrpcBridgeTests {

    @Test
    @DisplayName("should map minimal CustomerSecret through gql to domain to grpc")
    void shouldMapMinimalCustomerSecretBridge() {
      var gql = new CustomerSecretInput()
          .setLocation("HQ");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerSecret("HQ", null, null, null, null));

      var changedWhen = LocalDateTime.of(2026, 3, 11, 9, 0, 0);
      var changedWho = "editor@example.com";
      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain, changedWhen, changedWho);

      var expectedGrpc = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("HQ")
          .setChangedWhen(sinnet.grpc.customers.LocalDateTime.newBuilder()
              .setYear(2026)
              .setMonth(3)
              .setDay(11)
              .setHour(9)
              .setMinute(0)
              .setSecond(0)
              .build())
          .setChangedWho("editor@example.com")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }

    @Test
    @DisplayName("should map full CustomerSecret through gql to domain to grpc")
    void shouldMapFullCustomerSecretBridge() {
      var gql = new CustomerSecretInput()
          .setLocation("Datacenter")
          .setUsername("ops")
          .setPassword("p@ss")
          .setOtpSecret("otp")
          .setOtpRecoveryKeys("k1,k2");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2"));

      var changedWhen = LocalDateTime.of(2026, 3, 11, 10, 11, 12);
      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain, changedWhen, "alice@example.com");

      var expectedGrpc = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Datacenter")
          .setUsername("ops")
          .setPassword("p@ss")
          .setChangedWhen(sinnet.grpc.customers.LocalDateTime.newBuilder()
              .setYear(2026)
              .setMonth(3)
              .setDay(11)
              .setHour(10)
              .setMinute(11)
              .setSecond(12)
              .build())
          .setChangedWho("alice@example.com")
          .setOtpSecret("otp")
          .setOtpRecoveryKeys("k1,k2")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }

    @Test
    @DisplayName("should map minimal CustomerSecretEx through gql to domain to grpc")
    void shouldMapMinimalCustomerSecretExBridge() {
      var gql = new CustomerSecretExInput()
          .setLocation("Branch");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerSecretEx("Branch", null, null, null, null, null, null));

      var changedWhen = LocalDateTime.of(2026, 3, 11, 11, 0, 0);
      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain, changedWhen, "bob@example.com");

      var expectedGrpc = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("Branch")
          .setChangedWhen(sinnet.grpc.customers.LocalDateTime.newBuilder()
              .setYear(2026)
              .setMonth(3)
              .setDay(11)
              .setHour(11)
              .setMinute(0)
              .setSecond(0)
              .build())
          .setChangedWho("bob@example.com")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }

    @Test
    @DisplayName("should map full CustomerSecretEx through gql to domain to grpc")
    void shouldMapFullCustomerSecretExBridge() {
      var gql = new CustomerSecretExInput()
          .setLocation("HQ")
          .setUsername("root")
          .setPassword("very-secret")
          .setEntityName("EHR")
          .setEntityCode("EHR-01")
          .setOtpSecret("otp-seed")
          .setOtpRecoveryKeys("r1,r2");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2"));

      var changedWhen = LocalDateTime.of(2026, 3, 11, 12, 13, 14);
      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain, changedWhen, "carol@example.com");

      var expectedGrpc = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("HQ")
          .setUsername("root")
          .setPassword("very-secret")
          .setEntityName("EHR")
          .setEntityCode("EHR-01")
          .setChangedWhen(sinnet.grpc.customers.LocalDateTime.newBuilder()
              .setYear(2026)
              .setMonth(3)
              .setDay(11)
              .setHour(12)
              .setMinute(13)
              .setSecond(14)
              .build())
          .setChangedWho("carol@example.com")
          .setOtpSecret("otp-seed")
          .setOtpRecoveryKeys("r1,r2")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }

    @Test
    @DisplayName("should map minimal CustomerContact through gql to domain to grpc")
    void shouldMapMinimalCustomerContactBridge() {
      var gql = new CustomerContactInputGql();
      gql.setFirstName("Ada");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerContact("Ada", null, null, null));

      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain);

      var expectedGrpc = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Ada")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }

    @Test
    @DisplayName("should map full CustomerContact through gql to domain to grpc")
    void shouldMapFullCustomerContactBridge() {
      var gql = new CustomerContactInputGql();
      gql.setFirstName("Jan");
      gql.setLastName("Kowalski");
      gql.setPhoneNo("+48123123123");
      gql.setEmail("jan.kowalski@example.com");

      var domain = mapper.toDomain(gql);

      assertThat(domain).isEqualTo(new CustomerContact("Jan", "Kowalski", "+48123123123", "jan.kowalski@example.com"));

      var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(domain);

      var expectedGrpc = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Jan")
          .setLastName("Kowalski")
          .setPhoneNo("+48123123123")
          .setEmail("jan.kowalski@example.com")
          .build();
      assertThat(grpc).isEqualTo(expectedGrpc);
    }
  }

  @Nested
  @DisplayName("domain -> gql mappings")
  class DomainToGqlTests {

    @Test
    @DisplayName("should map domain EntityId to SomeEntityGql")
    void shouldMapDomainEntityIdToSomeEntityGql() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var source = new EntityId(projectId, entityId, 7L);

      var result = mapper.toGql(source);

      assertThat(result).isNotNull();
      assertThat(result.getProjectId()).isEqualTo(projectId.toString());
      assertThat(result.getEntityId()).isEqualTo(entityId.toString());
      assertThat(result.getEntityVersion()).isEqualTo(7L);
    }

    @Test
    @DisplayName("should return null for null domain EntityId")
    void shouldReturnNullForNullDomainEntityId() {
      assertThat(mapper.toGql((sinnet.domain.models.EntityId) null)).isNull();
    }

    @Test
    @DisplayName("should map domain CustomerContact to gql")
    void shouldMapDomainContactToGql() {
      var minimal = new CustomerContact("Ada", null, null, null);
      var full = new CustomerContact("Jan", "Kowalski", "+48123123123", "jan@example.com");

      var minResult = mapper.toGql(minimal);
      assertThat(minResult.getFirstName()).isEqualTo("Ada");
      assertThat(minResult.getLastName()).isNull();
      assertThat(minResult.getPhoneNo()).isNull();
      assertThat(minResult.getEmail()).isNull();

      var fullResult = mapper.toGql(full);
      assertThat(fullResult.getFirstName()).isEqualTo("Jan");
      assertThat(fullResult.getLastName()).isEqualTo("Kowalski");
      assertThat(fullResult.getPhoneNo()).isEqualTo("+48123123123");
      assertThat(fullResult.getEmail()).isEqualTo("jan@example.com");
    }

    @Test
    @DisplayName("should return null for null domain CustomerContact")
    void shouldReturnNullForNullDomainContact() {
      assertThat(mapper.toGql((CustomerContact) null)).isNull();
    }

    @Test
    @DisplayName("should map domain CustomerSecret to gql with placeholder dates")
    void shouldMapDomainSecretToGql() {
      var minimal = new CustomerSecret("HQ", null, null, null, null);
      var full = new CustomerSecret("DC", "ops", "p@ss", "otp", "k1,k2");

      var minResult = mapper.toGql(minimal);
      assertThat(minResult.getLocation()).isEqualTo("HQ");
      assertThat(minResult.getChangedWhen()).isEqualTo("?");
      assertThat(minResult.getChangedWho()).isEqualTo("?");

      var fullResult = mapper.toGql(full);
      assertThat(fullResult.getLocation()).isEqualTo("DC");
      assertThat(fullResult.getUsername()).isEqualTo("ops");
      assertThat(fullResult.getPassword()).isEqualTo("p@ss");
      assertThat(fullResult.getOtpSecret()).isEqualTo("otp");
      assertThat(fullResult.getOtpRecoveryKeys()).isEqualTo("k1,k2");
      assertThat(fullResult.getChangedWhen()).isEqualTo("?");
    }

    @Test
    @DisplayName("should return null for null domain CustomerSecret")
    void shouldReturnNullForNullDomainSecret() {
      assertThat(mapper.toGql((CustomerSecret) null)).isNull();
    }

    @Test
    @DisplayName("should map domain CustomerSecretEx to gql with placeholder dates")
    void shouldMapDomainSecretExToGql() {
      var minimal = new CustomerSecretEx("Branch", null, null, null, null, null, null);
      var full = new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2");

      var minResult = mapper.toGql(minimal);
      assertThat(minResult.getLocation()).isEqualTo("Branch");
      assertThat(minResult.getChangedWhen()).isEqualTo("?");
      assertThat(minResult.getChangedWho()).isEqualTo("?");

      var fullResult = mapper.toGql(full);
      assertThat(fullResult.getLocation()).isEqualTo("HQ");
      assertThat(fullResult.getUsername()).isEqualTo("root");
      assertThat(fullResult.getPassword()).isEqualTo("very-secret");
      assertThat(fullResult.getEntityName()).isEqualTo("EHR");
      assertThat(fullResult.getEntityCode()).isEqualTo("EHR-01");
      assertThat(fullResult.getOtpSecret()).isEqualTo("otp-seed");
      assertThat(fullResult.getOtpRecoveryKeys()).isEqualTo("r1,r2");
    }

    @Test
    @DisplayName("should return null for null domain CustomerSecretEx")
    void shouldReturnNullForNullDomainSecretEx() {
      assertThat(mapper.toGql((sinnet.domain.models.CustomerSecretEx) null)).isNull();
    }

    @Test
    @DisplayName("should map domain CustomerValue to CustomerModelGql")
    void shouldMapDomainCustomerValueToGql() {
      var entry = new CustomerEntry(
          "op@example.com", "subscription", "active", 15,
          "ACME Clinic", "Warsaw", "Main St 1",
          true, false, true, false, false, false, false, false, false, false,
          false, false, false, false, false, false,
          "NFZ notes", true, "Commercial notes", "Tech data");
      var value = new CustomerValue(entry, List.of(), List.of(), List.of());

      var result = mapper.toGql(value);

      assertThat(result).isNotNull();
      assertThat(result.getCustomerName()).isEqualTo("ACME Clinic");
      assertThat(result.getCustomerCityName()).isEqualTo("Warsaw");
      assertThat(result.getCustomerAddress()).isEqualTo("Main St 1");
      assertThat(result.getOperatorEmail()).isEqualTo("op@example.com");
      assertThat(result.getBillingModel()).isEqualTo("subscription");
      assertThat(result.getSupportStatus()).isEqualTo("active");
      assertThat(result.getDistance()).isEqualTo(15);
      assertThat(result.getNfzUmowa()).isTrue();
      assertThat(result.getNfzLekarz()).isTrue();
      assertThat(result.getKomercjaJest()).isTrue();
      assertThat(result.getNfzNotatki()).isEqualTo("NFZ notes");
      assertThat(result.getKomercjaNotatki()).isEqualTo("Commercial notes");
      assertThat(result.getDaneTechniczne()).isEqualTo("Tech data");
    }

    @Test
    @DisplayName("should return null for null domain CustomerValue")
    void shouldReturnNullForNullCustomerValue() {
      assertThat(mapper.toGql((CustomerValue) null)).isNull();
    }

    @Test
    @DisplayName("should map domain Customer to CustomerEntityGql with nested collections")
    void shouldMapDomainCustomerToGql() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var id = new EntityId(projectId, entityId, 5L);
      var entry = new CustomerEntry(
          null, null, null, 0, "Clinic", null, null,
          false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false,
          null, false, null, null);
      var secret = new CustomerSecret("DC", "admin", "pass", null, null);
      var secretEx = new CustomerSecretEx("HQ", "root", "x", "E", "E-01", null, null);
      var contact = new CustomerContact("Jan", "Nowak", "+48111222333", "jan@example.com");
      var value = new CustomerValue(entry, List.of(secret), List.of(secretEx), List.of(contact));
      var customer = new Customer(id, value);

      var result = mapper.toGql(customer);

      assertThat(result).isNotNull();
      assertThat(result.getId().getProjectId()).isEqualTo(projectId.toString());
      assertThat(result.getId().getEntityId()).isEqualTo(entityId.toString());
      assertThat(result.getId().getEntityVersion()).isEqualTo(5L);
      assertThat(result.getData().getCustomerName()).isEqualTo("Clinic");
      assertThat(result.getSecrets()).hasSize(1);
      assertThat(result.getSecrets()[0].getLocation()).isEqualTo("DC");
      assertThat(result.getSecretsEx()).hasSize(1);
      assertThat(result.getSecretsEx()[0].getEntityCode()).isEqualTo("E-01");
      assertThat(result.getContacts()).hasSize(1);
      assertThat(result.getContacts()[0].getFirstName()).isEqualTo("Jan");
    }

    @Test
    @DisplayName("should return null for null domain Customer")
    void shouldReturnNullForNullCustomer() {
      assertThat(mapper.toGql((Customer) null)).isNull();
    }

    @Test
    @DisplayName("should map domain Customer with empty collections")
    void shouldMapCustomerWithEmptyCollections() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var id = new EntityId(projectId, entityId, 0L);
      var entry = new CustomerEntry(
          null, null, null, 0, "Empty", null, null,
          false, false, false, false, false, false, false, false, false,
          false, false, false, false, false, false, false,
          null, false, null, null);
      var value = new CustomerValue(entry, List.of(), List.of(), List.of());
      var customer = new Customer(id, value);

      var result = mapper.toGql(customer);

      assertThat(result.getSecrets()).isEmpty();
      assertThat(result.getSecretsEx()).isEmpty();
      assertThat(result.getContacts()).isEmpty();
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

      var toGrpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(original);
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
        var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(dateTime);
        var result = mapper.map(grpc);
        assertThat(result).isEqualTo(dateTime);
      }
    }

    @Test
    @DisplayName("should handle complex workflow with random EntityGql")
    void shouldHandleComplexWorkflow() {
      var randomEntities = Instancio.ofList(EntityGql.class).size(3).create();

      for (var entity : randomEntities) {
        var grpc = sinnet.infra.adapters.grpc.CustomerMapper.toGrpc(entity);
        assertThat(grpc).isNotNull();
        assertThat(grpc.getEntityId()).isEqualTo(entity.getEntityId());
        assertThat(grpc.getEntityVersion()).isEqualTo(entity.getEntityVersion());
      }
    }
  }

}
