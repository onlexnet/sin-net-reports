package sinnet.customer;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import sinnet.AppTestContext;
import sinnet.commands.RegisterNewCustomer;
import sinnet.models.CustomerValue;
import sinnet.models.Name;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerServiceTests {

    @Autowired
    private CustomerRepository repository;

    @Test
    void someTest() {
        repository
            .save(UUID.randomUUID(), 1, CustomerValue.builder().build());
    }
}
