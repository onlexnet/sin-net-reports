package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sinnet.domain.models.Customer;
import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.EntityId;

@DisplayName("CustomerMapper (grpc adapter) Tests")
class CustomerMapperTest {

  @Nested
  @DisplayName("LocalDateTime -> grpc LocalDateTime")
  class DateTimeMappingTests {

    @Test
    @DisplayName("should map LocalDateTime")
    void shouldMapLocalDateTime() {
      var source = LocalDateTime.of(2024, 12, 25, 14, 30, 45);

      var result = CustomerMapper.toGrpc(source);

      assertThat(result).isEqualTo(sinnet.grpc.customers.LocalDateTime.newBuilder()
          .setYear(2024)
          .setMonth(12)
          .setDay(25)
          .setHour(14)
          .setMinute(30)
          .setSecond(45)
          .build());
    }

    @Test
    @DisplayName("should return null for null LocalDateTime")
    void shouldHandleNullDateTime() {
      assertThat(CustomerMapper.toGrpc((LocalDateTime) null)).isNull();
    }
  }

  @Nested
  @DisplayName("grpc -> domain mappings")
  class GrpcToDomainTests {

    @Test
    @DisplayName("should map grpc CustomerContact to domain")
    void shouldMapGrpcContactToDomain() {
      var minimal = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Ada")
          .build();
      var full = sinnet.grpc.customers.CustomerContact.newBuilder()
          .setFirstName("Jan")
          .setLastName("Kowalski")
          .setPhoneNo("+48123123123")
          .setEmail("jan@example.com")
          .build();

      assertThat(CustomerMapper.toDomain(minimal)).isEqualTo(new CustomerContact("Ada", "", "", ""));
      assertThat(CustomerMapper.toDomain(full)).isEqualTo(new CustomerContact("Jan", "Kowalski", "+48123123123", "jan@example.com"));
    }

    @Test
    @DisplayName("should return null for null grpc CustomerContact")
    void shouldReturnNullForNullContact() {
      assertThat(CustomerMapper.toDomain((sinnet.grpc.customers.CustomerContact) null)).isNull();
    }

    @Test
    @DisplayName("should map grpc CustomerSecret to domain")
    void shouldMapGrpcSecretToDomain() {
      var minimal = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("HQ")
          .build();
      var full = sinnet.grpc.customers.CustomerSecret.newBuilder()
          .setLocation("Datacenter")
          .setUsername("ops")
          .setPassword("p@ss")
          .setOtpSecret("otp")
          .setOtpRecoveryKeys("k1,k2")
          .build();

      assertThat(CustomerMapper.toDomain(minimal)).isEqualTo(new CustomerSecret("HQ", "", "", "", "", "", null));
      assertThat(CustomerMapper.toDomain(full)).isEqualTo(new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2", "", null));
    }

    @Test
    @DisplayName("should return null for null grpc CustomerSecret")
    void shouldReturnNullForNullSecret() {
      assertThat(CustomerMapper.toDomain((sinnet.grpc.customers.CustomerSecret) null)).isNull();
    }

    @Test
    @DisplayName("should map grpc CustomerSecretEx to domain")
    void shouldMapGrpcSecretExToDomain() {
      var minimal = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("Branch")
          .build();
      var full = sinnet.grpc.customers.CustomerSecretEx.newBuilder()
          .setLocation("HQ")
          .setUsername("root")
          .setPassword("very-secret")
          .setEntityName("EHR")
          .setEntityCode("EHR-01")
          .setOtpSecret("otp-seed")
          .setOtpRecoveryKeys("r1,r2")
          .build();

      assertThat(CustomerMapper.toDomain(minimal)).isEqualTo(new CustomerSecretEx("Branch", "", "", "", "", "", "", "", null));
      assertThat(CustomerMapper.toDomain(full)).isEqualTo(new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2", "", null));
    }

    @Test
    @DisplayName("should return null for null grpc CustomerSecretEx")
    void shouldReturnNullForNullSecretEx() {
      assertThat(CustomerMapper.toDomain((sinnet.grpc.customers.CustomerSecretEx) null)).isNull();
    }

    @Test
    @DisplayName("should map full grpc CustomerModel to domain Customer")
    void shouldMapGrpcCustomerModelToDomain() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var grpcModel = sinnet.grpc.customers.CustomerModel.newBuilder()
          .setId(sinnet.grpc.common.EntityId.newBuilder()
              .setProjectId(projectId.toString())
              .setEntityId(entityId.toString())
              .setEntityVersion(3L)
              .build())
          .setValue(sinnet.grpc.customers.CustomerValue.newBuilder()
              .setCustomerName("ACME Clinic")
              .setCustomerCityName("Warsaw")
              .setCustomerAddress("Main St 1")
              .setOperatorEmail("op@example.com")
              .build())
          .addSecrets(sinnet.grpc.customers.CustomerSecret.newBuilder()
              .setLocation("DC")
              .setUsername("admin")
              .build())
          .addSecretEx(sinnet.grpc.customers.CustomerSecretEx.newBuilder()
              .setLocation("HQ")
              .setEntityCode("E-01")
              .build())
          .addContacts(sinnet.grpc.customers.CustomerContact.newBuilder()
              .setFirstName("Jan")
              .setEmail("jan@example.com")
              .build())
          .build();

      var result = CustomerMapper.toDomain(grpcModel);

      assertThat(result).isEqualTo(new Customer(
          new EntityId(projectId, entityId, 3L),
          new CustomerValue(
              new CustomerEntry("op@example.com", "", "", 0, "ACME Clinic", "Warsaw", "Main St 1",
                  false, false, false, false, false, false, false, false, false, false,
                  false, false, false, false, false, false,
                  "", false, "", ""),
              List.of(new CustomerSecret("DC", "admin", "", "", "", "", null)),
              List.of(new CustomerSecretEx("HQ", "", "", "", "E-01", "", "", "", null)),
              List.of(new CustomerContact("Jan", "", "", "jan@example.com")))));
    }

    @Test
    @DisplayName("should return null for null grpc CustomerModel")
    void shouldReturnNullForNullModel() {
      assertThat(CustomerMapper.toDomain((sinnet.grpc.customers.CustomerModel) null)).isNull();
    }

    @Test
    @DisplayName("should map grpc CustomerModel with empty lists to domain")
    void shouldMapGrpcCustomerModelWithEmptyLists() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var grpcModel = sinnet.grpc.customers.CustomerModel.newBuilder()
          .setId(sinnet.grpc.common.EntityId.newBuilder()
              .setProjectId(projectId.toString())
              .setEntityId(entityId.toString())
              .setEntityVersion(0L)
              .build())
          .setValue(sinnet.grpc.customers.CustomerValue.newBuilder()
              .setCustomerName("Minimal")
              .build())
          .build();

      var result = CustomerMapper.toDomain(grpcModel);

      assertThat(result).isEqualTo(new Customer(
          new EntityId(projectId, entityId, 0L),
          new CustomerValue(
              new CustomerEntry("", "", "", 0, "Minimal", "", "",
                  false, false, false, false, false, false, false, false, false, false,
                  false, false, false, false, false, false,
                  "", false, "", ""),
              List.of(),
              List.of(),
              List.of())));
    }

    @Test
    @DisplayName("should return null CustomerValue for null grpc CustomerValue")
    void shouldReturnNullForNullCustomerValue() {
      assertThat(CustomerMapper.toDomain(null, List.of(), List.of(), List.of())).isNull();
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
      var minimal = new CustomerSecret("HQ", null, null, null, null, null, null);
      var full = new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2", null, null);
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
      var minimal = new CustomerSecretEx("Branch", null, null, null, null, null, null, null, null);
      var full = new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2", null, null);
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
