package sinnet.access;

import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.grpc.StatusException;
import io.quarkus.test.junit.QuarkusTest;
import sinnet.grpc.projects.UserToken;
import sinnet.model.ValProjectId;

@QuarkusTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AccessFacadeTest {

  @Inject
  AccessFacade accessFacade;

  @Test
  void should_reject_invalid_owner() {
    var requestor = UserToken.newBuilder()
        .setRequestorEmail("my@email")
        .build();
    var eid = ValProjectId.of(UUID.randomUUID());

    Assertions
      .assertThatCode(() -> accessFacade.guardAccess(requestor, eid, a -> a::canDeleteProject).await().indefinitely())
      .hasCauseInstanceOf(StatusException.class)
      .hasMessageContaining("FAILED_PRECONDITION");
  }
}
