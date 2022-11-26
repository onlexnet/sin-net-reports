package sinnet.user;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import sinnet.db.PostgresDbExtension;

@DisplayNameGeneration(DisplayNameGenerator.IndicativeSentences.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PostgresDbExtension.class)
class UserApiTest {
  
  @Test
  void initializeContext() {
    
  }
}
