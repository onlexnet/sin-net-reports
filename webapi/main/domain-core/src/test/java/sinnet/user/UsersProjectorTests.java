package sinnet.user;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import lombok.SneakyThrows;
import sinnet.ActionRepository;
import sinnet.Api;
import sinnet.AppTestContext;
import sinnet.Sync;
import sinnet.UsersProjector;
import sinnet.models.ActionValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.read.ActionProjector;
import sinnet.read.ProjectProjector;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public final class UsersProjectorTests implements ProjectProjector {

  @Autowired
  private UsersProjector sut;

  @Autowired
  private Api api;

  @Autowired
  private ProjectProjector.Provider projectProjector;

  @Autowired
  private ActionRepository actionRepository;
  
  @Autowired
  private ActionProjector.Provider actionProjector;

  @Test
  public void myTest() {
    var actual = sut.search(UUID.fromString("00000000-0000-0000-0001-000000000001"), Email.of("user1@project1"))
        .toCompletionStage().toCompletableFuture().join()
        .map(it -> it.getEmail().getValue());

    Assertions.assertThat(actual).containsExactlyInAnyOrder(
        "user1@project1",
        "user2@project1",
        "siudeks@gmail.com");
  }

  @Test
  @SneakyThrows
  public void myTest2() {
    var projectId = api.createNewProject();
    var randomPart = UUID.randomUUID().toString();
    var emailOfInvitedServiceman = randomPart + "@test";
    api.assignToProject(emailOfInvitedServiceman, "name of the serviceman", projectId);

    var now = LocalDate.now();
    var action = ActionValue.builder()
        .who(Email.of(emailOfInvitedServiceman))
        .when(now)
        .build();
    var actionId = EntityId.anyNew(projectId);
    Sync.of(() -> actionRepository.save(actionId, action)).get();

    var actual = Sync.of(() -> actionProjector.find(projectId.getId(), now, now)).get();
    var expected = ActionProjector.ListItem.builder()
        .eid(actionId)
        .value(action)
        .servicemanName("name of the serviceman")
        .build();
    Assertions.assertThat(actual).containsExactly(expected);
  }

}
