package sinnet.customer;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import sinnet.AppTestContext;
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
        var exitGuard = new CompletableFuture<Boolean>();

        var model = CustomerValue.builder()
                                 .customerName(Name.of("some not-empty name"))
                                 .build();
        var result = repository
            .save(EntityId.some(), model);
        result
            .onSuccess(it -> exitGuard.complete(it))
            .onFailure(ex -> exitGuard.completeExceptionally(ex));

        Assertions.assertThat(exitGuard.join()).isEqualTo(Boolean.TRUE);
    }

    @Test
    void saveFullModel() {
        var exitGuard1 = new CompletableFuture<Boolean>();

        var model = CustomerValue.builder()
                                 .customerName(Name.of("some not-empty name"))
                                 .customerCityName(Name.of("some city"))
                                 .customerAddress("Some address")
                                 .build();
        var someId = EntityId.some();
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
}
