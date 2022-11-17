package sinnet.read;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import sinnet.domain.project.Project;
import sinnet.models.Email;

/**
 * Default implementation of {@see ProjectProjector.Provider}.
 */
@Component
@RequiredArgsConstructor
public class ProjectProjectorProvider implements ProjectProjector, ProjectProjector.Provider {

  private final Project.Repository projectRepository;

  @Override
  public Array<FindByServicemanModel> findByServiceman(Email email) {
    return projectRepository
      .get(email.getValue())
      .map(it -> new FindByServicemanModel(it.getId(), it.getName()));
  }
  
}
