package sinnet.adapters.gql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sinnet.adapters.gql.CustomerMapper;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerSecretEx;
import sinnet.domain.models.CustomerValue;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;

@DisplayName("CustomerMapper (gql adapter) tests")
class CustomerMapperTest {

  private final CustomerMapper mapper = CustomerMapper.INSTANCE;

  @Nested
  @DisplayName("gql -> domain mappings")
  class GqlToDomainTests {

    @Test
    @DisplayName("should map CustomerInput with defaults")
    void shouldMapCustomerInputWithDefaults() {
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
    @DisplayName("should map secrets and contacts inputs")
    void shouldMapSecretAndContactInputs() {
      var secretInput = new CustomerSecretInput();
      secretInput.setLocation("Datacenter");
      secretInput.setUsername("ops");
      secretInput.setPassword("p@ss");
      secretInput.setOtpSecret("otp");
      secretInput.setOtpRecoveryKeys("k1,k2");

      var secretExInput = new CustomerSecretExInput();
      secretExInput.setLocation("HQ");
      secretExInput.setUsername("root");
      secretExInput.setPassword("very-secret");
      secretExInput.setEntityName("EHR");
      secretExInput.setEntityCode("EHR-01");
      secretExInput.setOtpSecret("otp-seed");
      secretExInput.setOtpRecoveryKeys("r1,r2");

      var contactInput = new CustomerContactInputGql();
      contactInput.setFirstName("Jan");
      contactInput.setLastName("Kowalski");
      contactInput.setPhoneNo("+48123123123");
      contactInput.setEmail("jan.kowalski@example.com");

      assertThat(mapper.toDomain(secretInput)).isEqualTo(new CustomerSecret("Datacenter", "ops", "p@ss", "otp", "k1,k2"));
      assertThat(mapper.toDomain(secretExInput)).isEqualTo(new CustomerSecretEx("HQ", "root", "very-secret", "EHR", "EHR-01", "otp-seed", "r1,r2"));
      assertThat(mapper.toDomain(contactInput)).isEqualTo(new CustomerContact("Jan", "Kowalski", "+48123123123", "jan.kowalski@example.com"));
    }

    @Test
    @DisplayName("should map EntityGql to domain EntityId")
    void shouldMapEntityGqlToDomainEntityId() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var source = new EntityGql();
      source.setProjectId(projectId.toString());
      source.setEntityId(entityId.toString());
      source.setEntityVersion(7L);

      var result = mapper.map(source);

      assertThat(result).isEqualTo(new sinnet.domain.models.EntityId(projectId, entityId, 7L));
    }
  }

  @Nested
  @DisplayName("domain -> gql mappings")
  class DomainToGqlTests {

    @Test
    @DisplayName("should map customer secret and set unknown audit fields")
    void shouldMapSecretToGql() {
      var source = new CustomerSecret("Office", "user", "pass", "otp", "k1");

      var result = mapper.toGql(source);

      assertThat(result.getLocation()).isEqualTo("Office");
      assertThat(result.getUsername()).isEqualTo("user");
      assertThat(result.getPassword()).isEqualTo("pass");
      assertThat(result.getChangedWhen()).isEqualTo("?");
      assertThat(result.getChangedWho()).isEqualTo("?");
      assertThat(result.getOtpSecret()).isEqualTo("otp");
      assertThat(result.getOtpRecoveryKeys()).isEqualTo("k1");
    }

    @Test
    @DisplayName("should map customer secretEx and set unknown audit fields")
    void shouldMapSecretExToGql() {
      var source = new CustomerSecretEx("HQ", "admin", "secret", "Main", "E-1", "otp2", "k2");

      var result = mapper.toGql(source);

      assertThat(result.getLocation()).isEqualTo("HQ");
      assertThat(result.getEntityName()).isEqualTo("Main");
      assertThat(result.getEntityCode()).isEqualTo("E-1");
      assertThat(result.getChangedWhen()).isEqualTo("?");
      assertThat(result.getChangedWho()).isEqualTo("?");
    }

    @Test
    @DisplayName("should map full CustomerGetResult to CustomerEntityGql")
    void shouldMapCustomerGetResultToGql() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var source = new CustomerGetResult(
          new sinnet.domain.models.EntityId(projectId, entityId, 3L),
          new CustomerValue(
              new CustomerEntry(
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
                  "Tech data"),
              List.of(new CustomerSecret("Office", "user", "pass", "otp", "rk")),
              List.of(new CustomerSecretEx("HQ", "admin", "secret", "Main", "E-1", "otp2", "rk2")),
              List.of(new CustomerContact("Ada", "Lovelace", "+48123123123", "ada@example.com"))));

      var result = mapper.toGql(source);

      assertThat(result.getId().getProjectId()).isEqualTo(projectId.toString());
      assertThat(result.getId().getEntityId()).isEqualTo(entityId.toString());
      assertThat(result.getId().getEntityVersion()).isEqualTo(3L);
      assertThat(result.getData().getCustomerName()).isEqualTo("ACME Clinic");
      assertThat(result.getSecrets()).hasSize(1);
      assertThat(result.getSecretsEx()).hasSize(1);
      assertThat(result.getContacts()).hasSize(1);
      assertThat(result.getContacts()[0].getEmail()).isEqualTo("ada@example.com");
    }

    @Test
    @DisplayName("should map CustomerValue by delegating to entry")
    void shouldMapCustomerValueToGql() {
      var source = new CustomerValue(
          new CustomerEntry(
              "op@example.com",
              "subscription",
              "active",
              1,
              "Clinic",
              "City",
              "Address",
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
              null),
          List.of(),
          List.of(),
          List.of());

      var result = mapper.toGql(source);

      assertThat(result.getCustomerName()).isEqualTo("Clinic");
      assertThat(result.getBillingModel()).isEqualTo("subscription");
      assertThat(result.getDistance()).isEqualTo(1);
    }

    @Test
    @DisplayName("should map domain EntityId to SomeEntityGql")
    void shouldMapEntityIdToSomeEntityGql() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var source = new sinnet.domain.models.EntityId(projectId, entityId, 9L);

      var result = mapper.toGql(source);

      assertThat(result.getProjectId()).isEqualTo(projectId.toString());
      assertThat(result.getEntityId()).isEqualTo(entityId.toString());
      assertThat(result.getEntityVersion()).isEqualTo(9L);
    }
  }
}
