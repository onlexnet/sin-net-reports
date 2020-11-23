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
import sinnet.models.Name;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class CustomerDbServiceTests {

    @Autowired
    private PgPool pgPool;

    @Autowired
    private Vertx vertx;

    @Test
    void someTest() {
        var testContext = new VertxTestContext();

        var sut = new CustomerDbService(pgPool);
        vertx.deployVerticle(sut, testContext.succeeding(id -> {
            var cmd = new RegisterNewCustomer(
                UUID.randomUUID(),
                Name.of("a"),
                Name.of("b"),
                "c")
                .json();

            vertx.eventBus()
                .request(RegisterNewCustomer.ADDRESS,
                         cmd,
                         testContext.succeeding(reply -> { })
                );
        }));
    }
}
