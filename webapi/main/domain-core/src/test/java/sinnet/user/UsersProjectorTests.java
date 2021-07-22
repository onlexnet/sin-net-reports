package sinnet.user;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import io.vavr.collection.Array;
import lombok.SneakyThrows;
import sinnet.Api;
import sinnet.AppTestContext;
import sinnet.UsersProjector;
import sinnet.models.Email;
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

  @Test
  public void myTest() {
    var actual = sut.search(UUID.fromString("00000000-0000-0000-0001-000000000001"), Email.of("user1@project1"))
        .block()
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

    var actual = projectProjector.findByServiceman(Email.of(emailOfInvitedServiceman)).result();

    var expected = new FindByServicemanModel(projectId, "aaa");
    Assertions.assertThat(actual).containsExactly(expected);
  }

}
