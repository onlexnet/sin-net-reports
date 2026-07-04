package sinnet.dbo;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValProjectId;

@Component
@RequiredArgsConstructor
class DboRemoveImpl implements DboRemove {

  private final ProjectRepository repository;

  @Override
  public void remove(ValProjectId idHolder) {

    var eid = idHolder.value();
    repository.deleteById(eid);
  }
  
}
