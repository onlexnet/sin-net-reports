package sinnet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FailingTest {

    @Test
    public void myFailingTest() {
        Assertions.fail("Expected fail to prepare proper CICI pipeline");
    }
}
