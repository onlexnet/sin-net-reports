package sinnet.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.Value;

@ExtendWith(VertxExtension.class)
public class CustomerServiceTests {


    static Context newContext() {
        var repo = Mockito.mock(CustomerRepository.class);
        var sut = new CustomerService(repo);
        return new Context(sut, repo);
    }

    @Test
    void someTest(Vertx vertx, VertxTestContext testContext) {
        var ctx = newContext();

        testContext.completeNow();
    }

    @Value
    static class Context {
        private CustomerService sut;
        private CustomerRepository repo;
    }
}
