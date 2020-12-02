package sinnet.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repository;

    @Test
    void saveMinModel() {
        var result = Sync
            .wait(() -> {
                var model = CustomerValue.builder()
                    .customerName(Name.of("some not-empty name"))
                    .build();
                return repository
                       .save(EntityId.anyNew(), model); })
            .get();

        Assertions.assertThat(result).isEqualTo(Boolean.TRUE);
    }

    @Test
    void saveFullModel() {
        var someId = EntityId.anyNew();
        var model = CustomerValue.builder()
            .customerName(Name.of("some not-empty name"))
            .customerCityName(Name.of("some city"))
            .customerAddress("Some address")
            .build();

        Sync
            .wait(() -> {
                return repository
                    .save(someId, model); })
            .checkpoint(it -> {
                Assertions.assertThat(it).isEqualTo(Boolean.TRUE); })
            .and(it -> repository.get(someId))
            .checkpoint(it -> Assertions.assertThat(it.getValue()).isEqualTo(model));
    }

    private static CustomerValue newModel() {
        return CustomerValue.builder()
                                 .customerName(Name.of("Non-epty name" + UUID.randomUUID()))
                                 .build();
    }

    @Test
    public void list() {
        var id1 = EntityId.anyNew();
        var id2 = EntityId.anyNew();
        var list = Sync
            .wait(() -> {
                var someModel = newModel();
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
