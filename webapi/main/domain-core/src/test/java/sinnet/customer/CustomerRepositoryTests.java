package sinnet.customer;

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

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repository;

    @Test
    void someTest() {
        var exitGuard = new CompletableFuture<Boolean>();

        var result = repository
            .save(UUID.randomUUID(), 1, CustomerValue.builder().build());
        result
            .onSuccess(it -> exitGuard.complete(it))
            .onFailure(ex -> exitGuard.completeExceptionally(ex));

        Assertions.assertThat(exitGuard.join()).isEqualTo(Boolean.TRUE);
    }
}
