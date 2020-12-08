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
        var result = Sync
            .when(() -> {
                var projectId = UUID.randomUUID();
                return projectRepository.save(projectId).map(it -> projectId); })
            .and((projectId) -> {
                var model = CustomerValue.builder()
                    .customerName(Name.of("some not-empty name"))
                    .build();
                return repository
                       .save(EntityId.anyNew(projectId), model); })
            .get();

        Assertions.assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    void saveFullModel() {

        var projectId = UUID.randomUUID();
        var someId = EntityId.anyNew(projectId);
        var model = CustomerValue.builder()
            .customerName(Name.of("some not-empty name"))
            .customerCityName(Name.of("some city"))
            .customerAddress("Some address")
            .build();

        Sync
            .when(() -> projectRepository.save(projectId))
            .and(ignored -> repository.save(someId, model))
            .checkpoint(it -> {
                Assertions.assertThat(it).isEqualTo(Boolean.TRUE); })
            .and(it -> repository.get(someId))
            .checkpoint(it -> Assertions.assertThat(it.getValue()).isEqualTo(model));
    }

    private static CustomerValue newModel(UUID projectId) {
        return CustomerValue.builder()
                                 .customerName(Name.of("Non-epty name" + UUID.randomUUID()))
                                 .build();
    }

    @Test
    public void list() {
        var projectId = UUID.randomUUID();
        var id1 = EntityId.anyNew(projectId);
        var id2 = EntityId.anyNew(projectId);
        var list = Sync
            .when(() -> projectRepository.save(projectId))
            .and(ignored -> {
                var someModel = newModel(projectId);
                return CompositeFuture
                    .all(repository.save(id1, someModel),
                        repository.save(id2, someModel))
                    .map(it -> true); })
            .and(it -> repository.list())
            .get();

        var actual = List.ofAll(list).map(it -> it.getEntityId());
        assertThat(actual).contains(id1.getId(), id2.getId());
    }
}
