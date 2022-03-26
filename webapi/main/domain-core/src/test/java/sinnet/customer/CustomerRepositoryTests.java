package sinnet.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.vavr.collection.Array;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.experimental.UtilityClass;
import sinnet.Api;
import sinnet.AppTestContext;
import sinnet.Sync;
import sinnet.TestUsers;
import sinnet.bus.commands.ChangeCustomerData;
import sinnet.bus.query.FindCustomers;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.Name;
import sinnet.read.CustomerProjection;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerRepositoryTests {

  @Autowired
  Api api;

  @Autowired
  CustomerProjection customerProjection;

  @Test
  void initialiListOfCustomersIsEmpty() {
    var projectId = api.createNewProject();
    var actual = api.queryCustomers(projectId, TestUsers.SYSTEM);
    Assertions.assertThat(actual).isEmpty();
  }


  @Test
  void saveMinModel() {
    var projectId = api.createNewProject();
    var model = Given.minValidModel();
    var customerId = api.defineCustomer(EntityId.anyNew(projectId.getId()), model);
    Assertions.assertThat(customerId).isNotNull();
  }

  @Test
  void saveFullModel() {
    var projectId = api.createNewProject();
    var model = Given.fullModel();
    var customerId = api.defineCustomer(EntityId.anyNew(projectId.getId()), model);
    Assertions.assertThat(customerId).isNotNull();
  }

  @Test
  public void list() {
    var projectId = api.createNewProject();
    var id1 = api.defineCustomer(EntityId.anyNew(projectId));
    var id2 = api.defineCustomer(EntityId.anyNew(projectId));
    var list = api.queryCustomers(projectId, TestUsers.SYSTEM);

    var actual = List.of(list).map(it -> it.getEntityId());
    assertThat(actual).contains(id1.getId(), id2.getId());
  }

  @Test
  public void shouldUpdate() {
    var projectId = api.createNewProject();
    var validModel = Given.minValidModel();
    var eidV1 = api.defineCustomer(EntityId.anyNew(projectId), validModel);
    var eidV2 = api.defineCustomer(eidV1, validModel);

    var expected = FindCustomers.CustomerData.builder()
        .projectId(eidV2.getProjectId())
        .entityId(eidV2.getId())
        .entityVersion(eidV2.getVersion())
        .value(validModel)
        .build();
    var list = api.queryCustomers(projectId, TestUsers.SYSTEM);
    Assertions
        .assertThat(list)
        .containsOnly(expected);
  }

  @Test
  public void shouldNotDeleteLastVersionIfNewSaveFailed() {
    var projectId = api.createNewProject();
    var validModel = Given.minValidModel();
    var eid = api.defineCustomer(EntityId.anyNew(projectId), validModel);

    var invalidModel = Given.invalidModel();
    Assertions
        .catchThrowable(() -> api.defineCustomer(eid, invalidModel));

    var expected = FindCustomers.CustomerData.builder()
        .projectId(eid.getProjectId())
        .entityId(eid.getId())
        .value(validModel)
        .entityVersion(eid.getVersion())
        .build();
    var list = api.queryCustomers(projectId, TestUsers.SYSTEM);
    Assertions
        .assertThat(list).containsOnly(expected);

  }

  @Test
  public void shouldNotSaveWhenVersionIsNotNext() {
    var projectId = api.createNewProject();

    var eid = api.defineCustomer(EntityId.anyNew(projectId));

    var invalidEid = EntityId.of(eid.getProjectId(), eid.getId(), eid.getVersion() + 1);
    
    Try.run(() -> api.defineCustomer(invalidEid));

    var value = CustomerValue.builder()
        .customerName(Name.of("some not-empty name"))
        .build();
    var expected = FindCustomers.CustomerData.builder()
        .projectId(eid.getProjectId())
        .entityId(eid.getId())
        .entityVersion(eid.getVersion())
        .value(value)
        .build();
    var list = api.queryCustomers(projectId, TestUsers.SYSTEM);
    Assertions
        .assertThat(list).containsOnly(expected);
  }

  @Nested
  public class ShouldSupportSecrets {

    @Test
    void write() {
      var projectId = api.createNewProject();

      var someId = EntityId.anyNew(projectId);
      var model = Given.minValidModel();
      var secretEntry = ChangeCustomerData.Secret.builder()
          .location("My location " + UUID.randomUUID())
          .username("My username " + UUID.randomUUID())
          .password("My password " + UUID.randomUUID())
          .build();
      var id = api.defineCustomer(someId, model, ArrayUtils.toArray(secretEntry), ArrayUtils.toArray(), ArrayUtils.toArray());
      var customer = Sync.of(customerProjection.get(id)).get().get().getSecrets();
      assertThat(customer).allSatisfy(it -> {
        Assertions.assertThat(it.getLocation()).isEqualTo(secretEntry.getLocation());
        Assertions.assertThat(it.getUsername()).isEqualTo(secretEntry.getUsername());
        Assertions.assertThat(it.getPassword()).isEqualTo(secretEntry.getPassword());
      });
    }

    // TODO
        // @Test
        // void delete() {
        //     var projectId = api.createNewProject();

        //     var someId = EntityId.anyNew(projectId);
        //     var model = Given.minValidModel();
        //     var auths = Given.multipleSecrets();
        //     var emptyAuths = Given.emptySecrets();
        //     var secretsEx = Given.emptySecretsEx();
        //     var contacts = Given.emptyContacts();

        //     Sync.of(() -> repository.write(someId, model, auths, secretsEx, contacts))
        //         .and(newId -> repository.write(newId, model, emptyAuths, secretsEx, contacts))
        //         .and(it -> repository.get(it))
        //         .checkpoint(it -> assertThat(it.get().getSecrets()).isEmpty());
        // }
    }

    @Nested
    public class ShouldSupportSecretsEx {
  
      @Test
      void write() {
        var projectId = api.createNewProject();
  
        var someId1 = EntityId.anyNew(projectId);
        var someId2 = EntityId.anyNew(projectId);
        var model = Given.minValidModel();
        var randomPart = UUID.randomUUID();
        var secretEntry = ChangeCustomerData.SecretEx.builder()
            .location("My location " + randomPart)
            .username("My username " + randomPart)
            .password("My password " + randomPart)
            .entityCode("code " + randomPart)
            .entityName("name " + randomPart)
            .build();
        api.defineCustomer(someId1, model, ArrayUtils.toArray(), ArrayUtils.toArray(secretEntry), ArrayUtils.toArray());
        api.defineCustomer(someId2, model, ArrayUtils.toArray(), ArrayUtils.toArray(secretEntry), ArrayUtils.toArray());
        var customers = Sync.of(customerProjection.list(projectId.getId())).get().flatMap(it -> Array.of(it.getSecretsEx()));
        assertThat(customers)
          .hasSize(2)
          .allSatisfy(it -> {
            Assertions.assertThat(it.getLocation()).isEqualTo(secretEntry.getLocation());
            Assertions.assertThat(it.getUsername()).isEqualTo(secretEntry.getUsername());
            Assertions.assertThat(it.getPassword()).isEqualTo(secretEntry.getPassword());
            Assertions.assertThat(it.getEntityCode()).isEqualTo(secretEntry.getEntityCode());
            Assertions.assertThat(it.getEntityName()).isEqualTo(secretEntry.getEntityName());
        });
      }
  
  }
}

@UtilityClass
class Given {
    static CustomerValue minValidModel() {
        return CustomerValue.builder()
        .customerName(Name.of("some not-empty name"))
        .build();
    }

    static CustomerSecret[] emptySecrets() {
        return new CustomerSecret[0];
    }

    static CustomerSecretEx[] emptySecretsEx() {
        return new CustomerSecretEx[0];
    }

    static CustomerContact[] emptyContacts() {
        return new CustomerContact[0];
    }



    static CustomerSecret[] multipleSecrets() {
        var auth = (Supplier<CustomerSecret>) () -> CustomerSecret.builder()
            .location("My location " + UUID.randomUUID())
            .username("My username " + UUID.randomUUID())
            .password("My password " + UUID.randomUUID())
            .changedWhen(LocalDateTime.now())
            .changedWho(Email.of(UUID.randomUUID().toString()))
            .build();
        return new CustomerSecret[] {auth.get(), auth.get()};
    }

    static CustomerValue invalidModel() {
        return CustomerValue.builder()
        .build();
    }

  static CustomerValue fullModel() {
    var addresWithMaxLength = "Some address with max length";
    addresWithMaxLength += StringUtils.repeat("X", 100 - addresWithMaxLength.length());

    return CustomerValue.builder()
        .operatorEmail("operatorEmail")
        .billingModel("billingModel")
        .supportStatus("supportStatus")
        .customerName(Name.of("some not-empty name"))
        .customerCityName(Name.of("some city name"))
        .customerAddress(addresWithMaxLength)
        .nfzUmowa(true)
        .nfzMaFilie(true)
        .nfzLekarz(true)
        .nfzPolozna(true)
        .nfzPielegniarkaSrodowiskowa(true)
        .nfzMedycynaSzkolna(true)
        .nfzTransportSanitarny(true)
        .nfzNocnaPomocLekarska(true)
        .nfzAmbulatoryjnaOpiekaSpecjalistyczna(true)
        .nfzRehabilitacja(true)
        .nfzStomatologia(true)
        .nfzPsychiatria(true)
        .nfzSzpitalnictwo(true)
        .nfzProgramyProfilaktyczne(true)
        .nfzZaopatrzenieOrtopedyczne(true)
        .nfzOpiekaDlugoterminowa(true)
        .nfzNotatki("some notes")
        .komercjaJest(true)
        .komercjaNotatki("komercja notatki some notes")
        .daneTechniczne("dane techniczne some notes")
        .build();
    }
}
