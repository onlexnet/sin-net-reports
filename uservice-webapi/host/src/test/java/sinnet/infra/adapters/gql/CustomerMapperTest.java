package sinnet.infra.adapters.gql;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;

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
}
