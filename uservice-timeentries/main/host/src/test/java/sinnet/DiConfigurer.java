package sinnet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import sinnet.db.PostgresDbExtension;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PostgresDbExtension.class)
public class DiConfigurer {
}
