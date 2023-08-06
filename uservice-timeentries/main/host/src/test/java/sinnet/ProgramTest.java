package sinnet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import sinnet.db.SqlServerDbExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SqlServerDbExtension.class)
@ActiveProfiles("test")
public class ProgramTest {
  
  @Test
  void initializeContext() {
  }

}
