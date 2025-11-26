package sinnet.dbo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import sinnet.db.SqlServerDbExtension;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SqlServerDbExtension.class)
@ActiveProfiles("test")
class ProjectRepositoryTest {

  @Autowired
  private ProjectRepository projectRepository;

  @Test
  void isOwner_whenUserIsOwner_shouldReturnTrue() {
    // given
    var emailOfOwner = "owner@example.com";
    var entityId = UUID.randomUUID();
    var project = new ProjectDbo()
        .setEntityId(entityId)
        .setEmailOfOwner(emailOfOwner)
        .setName("Test Project")
        .setOperators(Set.of());
    projectRepository.save(project);

    // when
    var result = projectRepository.isOwner(emailOfOwner, entityId);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void isOwner_whenUserIsNotOwner_shouldReturnFalse() {
    // given
    var emailOfOwner = "owner@example.com";
    var otherUser = "other@example.com";
    var entityId = UUID.randomUUID();
    var project = new ProjectDbo()
        .setEntityId(entityId)
        .setEmailOfOwner(emailOfOwner)
        .setName("Test Project")
        .setOperators(Set.of());
    projectRepository.save(project);

    // when
    var result = projectRepository.isOwner(otherUser, entityId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void isOwner_whenProjectDoesNotExist_shouldReturnFalse() {
    // given
    var emailOfOwner = "owner@example.com";
    var nonExistentId = UUID.randomUUID();

    // when
    var result = projectRepository.isOwner(emailOfOwner, nonExistentId);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void isOwner_whenEntityIdMatchesButEmailDoesNot_shouldReturnFalse() {
    // given
    var emailOfOwner = "owner@example.com";
    var wrongEmail = "wrong@example.com";
    var entityId = UUID.randomUUID();
    var project = new ProjectDbo()
        .setEntityId(entityId)
        .setEmailOfOwner(emailOfOwner)
        .setName("Test Project")
        .setOperators(Set.of());
    projectRepository.save(project);

    // when
    var result = projectRepository.isOwner(wrongEmail, entityId);

    // then
    assertThat(result).isFalse();
  }
}
