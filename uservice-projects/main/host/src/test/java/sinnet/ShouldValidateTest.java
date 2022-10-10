package sinnet;

import javax.inject.Inject;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ValidateNameTest {

  final int maximumSizeOfUserEmail = 50;

  @Inject
  AppOperations operations;
  
  @Test
  void too_long_name() {
    var tooLongEmail = RandomStringUtils.randomAlphanumeric(maximumSizeOfUserEmail + 1);
    Assertions
        .assertThatCode(() -> operations.create(tooLongEmail))
        .isInstanceOfSatisfying(StatusRuntimeException.class,
          ex -> Assertions.assertThat(ex.getStatus().getCode()).isEqualTo(Status.FAILED_PRECONDITION.getCode()));
  }

  @Test
  void maximum_name() {
    var longEmail = RandomStringUtils.randomAlphanumeric(maximumSizeOfUserEmail);
    Assertions
        .assertThatCode(() -> operations.create(longEmail))
        .doesNotThrowAnyException();
  }
}
