package sinnet.customer;

import java.time.LocalDateTime;

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
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
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
    public class MergeSecretShould {

        @Test
        public void replaceEmptySecrets() {
            var requested = new ChangeCustomer.Secret("location1", "username1", "password1");
            var requestor = Email.of("someone@somewhere");
            var when = LocalDateTime.now();
            var empty = new CustomerSecret[0];
            var actual = CustomerService.merge(requestor, when, ArrayUtils.toArray(requested), empty);
            var expected = ArrayUtils.toArray(new CustomerSecret("location1", "username1", "password1", requestor, when));
            Assertions.assertThat(actual).isEqualTo(expected);
        }

        @Test
        public void removeExistingSecrets() {
            var requestedEmpty = new ChangeCustomer.Secret[0];
            var requestor = Email.of("someone@somewhere");
            var when = LocalDateTime.now();
            var someExistinhAuths = ArrayUtils.toArray(new CustomerSecret("a", "b", "c", requestor, when));
            var actual = CustomerService.merge(requestor, when, requestedEmpty, someExistinhAuths);
            Assertions.assertThat(actual).isEmpty();
        }

        @Test
        public void updateChangedSecretsCase1() {
            var newRequestor = Email.of("new@requestor");
            var newDate = LocalDateTime.now();
            var requested1 = new ChangeCustomer.Secret("location", "username1", "password1");

            var oldRequestor = Email.of("old@requestor");
            var oldDate = LocalDateTime.now();
            var existing1 = new CustomerSecret("location", "username1", "c", oldRequestor, oldDate);

            var actual = CustomerService.merge(newRequestor, newDate,
                                               ArrayUtils.toArray(requested1),
                                               ArrayUtils.toArray(existing1));
            Assertions
                .assertThat(actual)
                .containsExactly(new CustomerSecret("location", "username1", "password1", newRequestor, newDate));
        }

        @Test
        public void updateChangedSecretCase2() {
            var newRequestor = Email.of("new@requestor");
            var newDate = LocalDateTime.now();
            var requested1 = new ChangeCustomer.Secret("location", "username1", "password1");

            var oldRequestor = Email.of("old@requestor");
            var oldDate = LocalDateTime.now();
            var existing1 = new CustomerSecret("location", "username1", "password1", oldRequestor, oldDate);

            var actual = CustomerService.merge(newRequestor, newDate,
                                               ArrayUtils.toArray(requested1),
                                               ArrayUtils.toArray(existing1));
            Assertions
                .assertThat(actual)
                .containsExactly(new CustomerSecret("location", "username1", "password1", oldRequestor, oldDate));
        }
    }

    @Value
    static class Context {
        private CustomerService sut;
        private CustomerRepository repo;
    }
}
