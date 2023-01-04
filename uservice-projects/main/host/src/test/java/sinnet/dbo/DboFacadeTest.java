package sinnet.dbo;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import sinnet.model.ValEmail;
import sinnet.model.ValProjectId;

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DboFacadeTest {

  @Autowired
  DboFacade dboFacade;

  @Test
  public void should_not_fail() {
    dboFacade.ownedAsId(ValEmail.of("non-existing-email"));
  }

  @Test
  @Disabled
  public void should_be_owner_on_created_project() {
    var id = ValProjectId.of(UUID.randomUUID());
    var name = ValEmail.of(UUID.randomUUID().toString());
    dboFacade.create(new DboCreate.CreateContent(id, name));
    var owned = dboFacade.ownedAsId(name);
    Assertions.assertThat(owned).isNotEmpty();
  }

}

