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
        var exitGuard1 = new CompletableFuture<Boolean>();

        var model = CustomerValue.builder()
                                 .customerName(Name.of("some not-empty name"))
                                 .customerCityName(Name.of("some city"))
                                 .customerAddress("Some address")
                                 .build();
        var someId = EntityId.anyNew();
        repository
            .save(someId, model)
            .onSuccess(it -> exitGuard1.complete(it))
            .onFailure(ex -> exitGuard1.completeExceptionally(ex));

        Assertions.assertThat(exitGuard1.join()).isEqualTo(Boolean.TRUE);

        var exitGuard2 = new CompletableFuture<CustomerValue>();
        repository
            .get(someId)
            .onSuccess(it -> exitGuard2.complete(model))
            .onFailure(ex -> exitGuard2.completeExceptionally(ex));
        Assertions.assertThat(exitGuard2.join()).isEqualTo(model);
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
