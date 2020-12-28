package sinnet.customer;

import java.time.LocalDate;

import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.Value;
import sinnet.Dates;
import sinnet.bus.commands.ChangeCustomer;
import sinnet.models.CustomerAuthorization;
import sinnet.models.Email;

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
        var sut = ctx.sut;
        vertx.deployVerticle(sut);

        testContext.completeNow();
    }

    @Nested
    public class MergeAuthorisationShould {

        @Test
        public void replaceEmptyAuthorisations() {
            var requested = new ChangeCustomer.Authorization("location1", "username1", "password1");
            var requestor = Email.of("someone@somewhere");
            var when = Dates.gen().head();
            var empty = new CustomerAuthorization[0];
            var actual = CustomerService.merge(requestor, when, ArrayUtils.toArray(requested), empty);
            var expected = ArrayUtils.toArray(new CustomerAuthorization("location1", "username1", "password1", requestor, when));
            Assertions.assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void removeExistingAuthorisations() {
            Assertions.assertThat(false).isEqualTo(true);
        }

        @Test
        public void updateChangedAuthorisations() {
            Assertions.assertThat(false).isEqualTo(true);
        }
    }

    @Value
    static class Context {
        private CustomerService sut;
        private CustomerRepository repo;
    }
}
