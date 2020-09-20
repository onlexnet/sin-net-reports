package sinnet.action;

import java.time.LocalDate;

import javax.activation.DataSource;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import sinnet.ActionService;
import sinnet.AppTestContext;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties",
    "/domain-core-test.properties"
})
public class ActionServiceTests {

    @Autowired
    private ActionService sut;

    @Test
    public void myTest() {
        Assertions.assertThat(sut).isNotNull();
        sut.find(LocalDate.now(), LocalDate.now());
    }
}
