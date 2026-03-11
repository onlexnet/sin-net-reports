package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

import java.time.LocalDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.gql.models.EntityGql;

@DisplayName("CustomerMapper (grpc adapter) Tests")
class CustomerMapperTest {

  @Nested
  @DisplayName("EntityGql -> grpc EntityId")
  class EntityMappingTests {

    @Test
    @DisplayName("should map EntityGql with full fields")
    void shouldMapEntityFull() {
      var source = Instancio.of(EntityGql.class)
          .set(field(EntityGql::getProjectId), "proj-123")
          .set(field(EntityGql::getEntityId), "ent-456")
          .set(field(EntityGql::getEntityVersion), 42L)
          .create();

      var result = CustomerMapper.toGrpc(source);

      assertThat(result.getProjectId()).isEqualTo("proj-123");
      assertThat(result.getEntityId()).isEqualTo("ent-456");
      assertThat(result.getEntityVersion()).isEqualTo(42L);
    }
  }

  @Nested
  @DisplayName("LocalDateTime -> grpc LocalDateTime")
  class DateTimeMappingTests {

    @Test
    @DisplayName("should map LocalDateTime")
    void shouldMapLocalDateTime() {
      var source = LocalDateTime.of(2024, 12, 25, 14, 30, 45);

      var result = CustomerMapper.toGrpc(source);

      assertThat(result.getYear()).isEqualTo(2024);
      assertThat(result.getMonth()).isEqualTo(12);
      assertThat(result.getDay()).isEqualTo(25);
      assertThat(result.getHour()).isEqualTo(14);
      assertThat(result.getMinute()).isEqualTo(30);
      assertThat(result.getSecond()).isEqualTo(45);
    }

    @Test
    @DisplayName("should return null for null LocalDateTime")
    void shouldHandleNullDateTime() {
      assertThat(CustomerMapper.toGrpc((LocalDateTime) null)).isNull();
    }
  }

  @Nested
  @DisplayName("domain -> grpc mappings")
  class DomainToGrpcTests {

    @Test
    @DisplayName("should map CustomerEntry minimal and full")
    void shouldMapCustomerEntry() {
      var minimal = new CustomerEntry(
          null, null, null, 0, "Minimal Customer", null, null,
          false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
          null, false, null, null);

      var full = new CustomerEntry(
          "operator@example.com", "subscription", "active", 15, "ACME Clinic", "Warsaw", "Main St 1",
          true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
          "NFZ notes", true, "Commercial notes", "Tech data");

      assertThat(CustomerMapper.toGrpc(minimal)).isEqualTo(
          sinnet.grpc.customers.CustomerValue.newBuilder().setCustomerName("Minimal Customer").build());

      assertThat(CustomerMapper.toGrpc(full)).isEqualTo(
          sinnet.grpc.customers.CustomerValue.newBuilder()
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
              .build());
    }

    @Test
    @DisplayName("should map minimal and full CustomerSecret")
    void shouldMapCustomerSecret() {
      var minimal = new CustomerSecret("HQ", null, null, null, null);
      var full = new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2");
      var when = LocalDateTime.of(2026, 3, 11, 10, 11, 12);

      assertThat(CustomerMapper.toGrpc(minimal, when, "editor@example.com")).isEqualTo(
          sinnet.grpc.customers.CustomerSecret.newBuilder()
              .setLocation("HQ")
              .setChangedWhen(CustomerMapper.toGrpc(when))
              .setChangedWho("editor@example.com")
              .build());

      assertThat(CustomerMapper.toGrpc(full, when, "alice@example.com")).isEqualTo(
          sinnet.grpc.customers.CustomerSecret.newBuilder()
              .setLocation("Datacenter")
              .setUsername("ops")
              .setPassword("p@ss")
              .setChangedWhen(CustomerMapper.toGrpc(when))
              .setChangedWho("alice@example.com")
              .setOtpSecret("otp")
              .setOtpRecoveryKeys("k1,k2")
              .build());
    }

    @Test
    @DisplayName("should map minimal and full CustomerSecretEx")
    void shouldMapCustomerSecretEx() {
      var minimal = new CustomerSecretEx("Branch", null, null, null, null, null, null);
      var full = new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2");
      var when = LocalDateTime.of(2026, 3, 11, 12, 13, 14);

      assertThat(CustomerMapper.toGrpc(minimal, when, "bob@example.com")).isEqualTo(
          sinnet.grpc.customers.CustomerSecretEx.newBuilder()
              .setLocation("Branch")
              .setChangedWhen(CustomerMapper.toGrpc(when))
              .setChangedWho("bob@example.com")
              .build());

      assertThat(CustomerMapper.toGrpc(full, when, "carol@example.com")).isEqualTo(
          sinnet.grpc.customers.CustomerSecretEx.newBuilder()
              .setLocation("HQ")
              .setUsername("root")
              .setPassword("very-secret")
              .setEntityName("EHR")
              .setEntityCode("EHR-01")
              .setChangedWhen(CustomerMapper.toGrpc(when))
              .setChangedWho("carol@example.com")
              .setOtpSecret("otp-seed")
              .setOtpRecoveryKeys("r1,r2")
              .build());
    }

    @Test
    @DisplayName("should map minimal and full CustomerContact")
    void shouldMapCustomerContact() {
      var minimal = new CustomerContact("Ada", null, null, null);
      var full = new CustomerContact("Jan", "Kowalski", "+48123123123", "jan.kowalski@example.com");

      assertThat(CustomerMapper.toGrpc(minimal)).isEqualTo(
          sinnet.grpc.customers.CustomerContact.newBuilder().setFirstName("Ada").build());

      assertThat(CustomerMapper.toGrpc(full)).isEqualTo(
          sinnet.grpc.customers.CustomerContact.newBuilder()
              .setFirstName("Jan")
              .setLastName("Kowalski")
              .setPhoneNo("+48123123123")
              .setEmail("jan.kowalski@example.com")
              .build());
    }
  }
}
