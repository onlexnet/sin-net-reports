package sinnet.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import sinnet.bus.commands.ChangeCustomerData;

import org.apache.commons.lang3.ArrayUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.vavr.collection.List;
import lombok.experimental.UtilityClass;
import sinnet.Api;
import sinnet.AppTestContext;
import sinnet.Sync;
import sinnet.bus.query.FindCustomers;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.Name;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerRepositoryTests {

  @Autowired
  Api api;

  @Autowired


  @Test
  void initialiListOfCustomersIsEmpty() {
    var projectId = api.createNewProject();
    var actual = api.queryCustomers(projectId);
    Assertions.assertThat(actual).isEmpty();
  }


  @Test
  void saveMinModel() {
    var projectId = api.createNewProject();
    var givenEntityId = EntityId.anyNew(projectId.getId());
    var model = Given.minValidModel();
    var customerId = api.changeCustomer(model);
    Assertions.assertThat(customerId).isNotNull();
  }

  @Test
  void saveFullModel() {
    var projectId = api.createNewProject();
    var givenEntityId = EntityId.anyNew(projectId.getId());
    var model = Given.fullModel();
    var customerId = api.changeCustomer(model);
    Assertions.assertThat(customerId).isNotNull();
  }

  @Test
  public void list() {
    var projectId = api.createNewProject();
    var someModel = Given.minValidModel();
    var id1 = api.defineCustomer(EntityId.anyNew(projectId));
    var id2 = api.defineCustomer(EntityId.anyNew(projectId));
    var list = api.queryCustomers(projectId);

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
        .build();
         //, validModel, Given.emptySecrets(), Given.emptySecretsEx(), Given.emptyContacts());
    var list = api.queryCustomers(projectId);
    Assertions
        .assertThat(list)
        .containsOnly(expected);
  }

  @Test
  public void shouldNotDeleteLastVersionIfNewSaveFailed() {
    var projectId = api.createNewProject();
    var eid = api.defineCustomer(EntityId.anyNew(projectId));

    var invalidModel = Given.invalidModel();
    Assertions
        .catchThrowable(() -> api.defineCustomer(eid, invalidModel));

    var expected = FindCustomers.CustomerData.builder()
        .projectId(eid.getProjectId())
        .entityId(eid.getId())
        .entityVersion(eid.getVersion())
        .build();
    // var expected = new CustomerRepository.CustomerModel(newEid, validModel, Given.emptySecrets(), Given.emptySecretsEx(), Given.emptyContacts());
    var list = api.queryCustomers(projectId);
    Assertions
      .assertThat(list).containsOnly(expected);

  }

  @Test
  public void shouldNotSaveWhenVersionIsNotNext() {
    var projectId = api.createNewProject();

    var eid = api.defineCustomer(EntityId.anyNew(projectId));

    var invalidEid = EntityId.of(eid.getProjectId(), eid.getId(), eid.getVersion() + 1);
    api.defineCustomer(invalidEid);

    var expected = FindCustomers.CustomerData.builder()
        .projectId(eid.getProjectId())
        .entityId(eid.getId())
        .entityVersion(eid.getVersion())
        .build();
    // var expected = new CustomerRepository.CustomerModel(nextId, validModel, Given.emptySecrets(), Given.emptySecretsEx(), Given.emptyContacts());
    var list = api.queryCustomers(projectId);
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
      var secrets = Given.multipleSecrets();
      var secret = ChangeCustomerData.Secret.builder()
          .location("My location " + UUID.randomUUID())
          .username("My username " + UUID.randomUUID())
          .password("My password " + UUID.randomUUID())
          .build();
      var id = api.defineCustomer(someId, model, ArrayUtils.toArray(secret), ArrayUtils.toArray(), ArrayUtils.toArray());
      var customer = api.queryCustomers(projectId);
      assertThat(customer).asList().containsOnly(secrets);
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
        return CustomerValue.builder()
        .operatorEmail("operatorEmail")
        .billingModel("billingModel")
        .supportStatus("supportStatus")
        .customerName(Name.of("some not-empty name"))
        .customerCityName(Name.of("some city"))
        .customerAddress("Some address")
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
