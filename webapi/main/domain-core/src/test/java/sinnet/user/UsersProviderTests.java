package sinnet.user;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import sinnet.AppTestContext;
import sinnet.UsersProvider;
import sinnet.models.Email;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public final class UsersProviderTests {

    @Autowired
    private UsersProvider sut;

    @Test
    public void myTest() {
        var actual = sut.search(UUID.fromString("00000000-0000-0000-0001-000000000001"), Email.of("user1@project1"))
            .block()
            .map(it -> it.getEmail().getValue());

        Assertions.assertThat(actual).containsExactlyInAnyOrder(
            "user1@project1",
            "user2@project1",
            "siudeks@gmail.com");
    }
}
