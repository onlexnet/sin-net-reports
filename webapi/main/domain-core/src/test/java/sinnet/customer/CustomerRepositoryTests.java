package sinnet.customer;

import static org.assertj.core.api.Assertions.assertThat;

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
import io.vertx.core.CompositeFuture;
import lombok.experimental.UtilityClass;
import sinnet.AppTestContext;
import sinnet.Sync;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;
import sinnet.models.Name;
import sinnet.read.ProjectRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerRepositoryTests {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private CustomerRepository repository;

    @Test
    void shouldSupportEmptyList() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId)).get();

        var actual = Sync.of(() -> repository.list(projectId)).get();
        Assertions.assertThat(actual).isEmpty();
    }


    @Test
    void saveMinModel() {
        var projectId = UUID.randomUUID();
        var givenEntityId = EntityId.anyNew(projectId);
        var actual = Sync
            .of(() -> projectRepository.save(projectId))
            .and(ignored -> {
                var model = Given.minValidModel();
                return repository
                       .write(givenEntityId, model, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts()); })
            .get();

        var expected = EntityId.of(projectId, givenEntityId.getId(), givenEntityId.getVersion() + 1);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void saveFullModel() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var someId = EntityId.anyNew(projectId);
        var model = Given.fullModel();

        Sync.of(() -> repository.write(someId, model, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts()))
            .and(it -> repository.get(it))
            .checkpoint(it -> assertThat(it.get().getValue()).isEqualTo(model));
    }

    @Test
    public void list() {
        var projectId = UUID.randomUUID();
        var id1 = EntityId.anyNew(projectId);
        var id2 = EntityId.anyNew(projectId);
        var list = Sync
            .of(() -> projectRepository.save(projectId))
            .and(ignored -> {
                var someModel = Given.minValidModel();
                return CompositeFuture
                    .all(repository.write(id1, someModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts()),
                        repository.write(id2, someModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts()))
                    .map(it -> true); })
            .and(it -> repository.list(projectId))
            .get();

        var actual = List.ofAll(list).map(it -> it.getId().getId());
        assertThat(actual).contains(id1.getId(), id2.getId());
    }

    @Test
    public void shouldUpdate() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var eidV1 = Sync.of(() -> repository.write(eid, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).get();
        var eidV2 = Sync.of(() -> repository.write(eidV1, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).get();

        var expected = new CustomerRepository.CustomerModel(eidV2, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts());
        Sync
            .of(() -> repository.list(projectId))
            .checkpoint(it -> Assertions
                                .assertThat(it)
                                .containsOnly(expected));
    }

    @Test
    public void shouldNotDeleteLastVersionIfNewSaveFailed() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var newEid = Sync.of(() -> repository.write(eid, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).get();

        var invalidModel = Given.invalidModel();
        Assertions
            .catchThrowable(() -> Sync.of(() -> repository.write(newEid, invalidModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).get());

        var expected = new CustomerRepository.CustomerModel(newEid, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts());
        var list = Sync.of(() -> repository.list(projectId)).get();
        Assertions
            .assertThat(list).containsOnly(expected);

    }

    @Test
    public void shouldNotSaveWhenVersionIsNotNext() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var nextId = Sync.of(() -> repository.write(eid, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).get();

        var invalidEid = EntityId.of(nextId.getProjectId(), nextId.getId(), nextId.getVersion() + 1);
        Sync.of(() -> repository.write(invalidEid, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts())).tryGet();

        var expected = new CustomerRepository.CustomerModel(nextId, validModel, Given.emptyAuth(), Given.emptySecretsEx(), Given.emptyContacts());
        Sync
            .of(() -> repository.list(projectId))
            .checkpoint(it -> Assertions.assertThat(it).containsOnly(expected));
    }

    @Nested
    public class ShouldSupportSecrets {

        @Test
        void write() {
            var projectId = UUID.randomUUID();
            Sync.of(() -> projectRepository.save(projectId));

            var someId = EntityId.anyNew(projectId);
            var model = Given.minValidModel();
            var auths = Given.multipleSecrets();
            var secretsEx = Given.emptySecretsEx();
            var contacts = Given.emptyContacts();

            Sync.of(() -> repository.write(someId, model, auths, secretsEx, contacts))
                .and(it -> repository.get(it))
                .checkpoint(it -> assertThat(it.get().getSecrets()).containsOnly(auths));
        }

        @Test
        void delete() {
            var projectId = UUID.randomUUID();
            Sync.of(() -> projectRepository.save(projectId));

            var someId = EntityId.anyNew(projectId);
            var model = Given.minValidModel();
            var auths = Given.multipleSecrets();
            var emptyAuths = Given.emptyAuth();
            var secretsEx = Given.emptySecretsEx();
            var contacts = Given.emptyContacts();

            Sync.of(() -> repository.write(someId, model, auths, secretsEx, contacts))
                .and(newId -> repository.write(newId, model, emptyAuths, secretsEx, contacts))
                .and(it -> repository.get(it))
                .checkpoint(it -> assertThat(it.get().getSecrets()).isEmpty());
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

    static CustomerSecret[] emptyAuth() {
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
        .build();
    }
}
