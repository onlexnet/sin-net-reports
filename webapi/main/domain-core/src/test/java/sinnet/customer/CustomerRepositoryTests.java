package sinnet.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.assertj.core.api.Assertions;
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
    void saveMinModel() {
        var projectId = UUID.randomUUID();
        var givenEntityId = EntityId.anyNew(projectId);
        var actual = Sync
            .of(() -> projectRepository.save(projectId))
            .and(ignored -> {
                var model = Given.minValidModel();
                return repository
                       .save(givenEntityId, model); })
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

        Sync.of(() -> repository.save(someId, model))
            .and(it -> repository.get(it))
            .checkpoint(it -> assertThat(it.getValue()).isEqualTo(model));
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
                    .all(repository.save(id1, someModel),
                        repository.save(id2, someModel))
                    .map(it -> true); })
            .and(it -> repository.list(projectId))
            .get();

        var actual = List.ofAll(list).map(it -> it.getEntityId());
        assertThat(actual).contains(id1.getId(), id2.getId());
    }

    @Test
    public void shouldUpdate() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var eidV1 = Sync.of(() -> repository.save(eid, validModel)).get();
        var eidV2 = Sync.of(() -> repository.save(eidV1, validModel)).get();

        Sync
            .of(() -> repository.list(projectId))
            .checkpoint(it -> Assertions.assertThat(it).containsOnly(validModel.withId(eidV2)));
    }

    @Test
    public void shouldNotDeleteLastVersionIfNewSaveFailed() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var newEid = Sync.of(() -> repository.save(eid, validModel)).get();

        var invalidModel = Given.invalidModel();
        Assertions
            .catchThrowable(() -> Sync.of(() -> repository.save(newEid, invalidModel)).get());

        var list = Sync.of(() -> repository.list(projectId)).get();
        Assertions
            .assertThat(list).containsOnly(validModel.withId(newEid));

    }

    @Test
    public void shouldNotSaveWhenVersionIsNotNext() {
        var projectId = UUID.randomUUID();
        Sync.of(() -> projectRepository.save(projectId));

        var validModel = Given.minValidModel();
        var eid = EntityId.anyNew(projectId);
        var nextId = Sync.of(() -> repository.save(eid, validModel)).get();

        var invalidEid = EntityId.of(nextId.getProjectId(), nextId.getId(), nextId.getVersion() + 1);
        Sync.of(() -> repository.save(invalidEid, validModel)).tryGet();

        Sync
            .of(() -> repository.list(projectId))
            .checkpoint(it -> Assertions.assertThat(it).containsOnly(validModel.withId(nextId)));
    }
}

@UtilityClass
class Given {
    static CustomerValue minValidModel() {
        return CustomerValue.builder()
        .customerName(Name.of("some not-empty name"))
        .build();
    }

    static CustomerValue invalidModel() {
        return CustomerValue.builder()
        .build();
    }

    static CustomerValue fullModel() {
        return CustomerValue.builder()
        .customerName(Name.of("some not-empty name"))
        .customerCityName(Name.of("some city"))
        .customerAddress("Some address")
        .build();
    }
}
