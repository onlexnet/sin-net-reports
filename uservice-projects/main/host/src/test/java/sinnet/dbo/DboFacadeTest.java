package sinnet.dbo;

import java.time.Duration;
import java.util.UUID;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import sinnet.model.Email;
import sinnet.model.ProjectIdHolder;

@QuarkusTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DboFacadeTest {

  @Inject
  DboFacade dboFacade;

  @Test
  public void should_not_fail() {
    dboFacade.ownedAsId(Email.of("non-existing-email")).await().indefinitely();
  }

  @Test
  @Disabled
  public void should_be_owner_on_created_project() {
    var id = ProjectIdHolder.of(UUID.randomUUID());
    var name = Email.of(UUID.randomUUID().toString());
    dboFacade.create(id, name).await().atMost(Duration.ofSeconds(3));
    var owned = dboFacade.ownedAsId(name).await().indefinitely();
    Assertions.assertThat(owned).isNotEmpty();
  }

}

