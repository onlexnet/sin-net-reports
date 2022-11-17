package sinnet.action;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.ShardedId;
import sinnet.write.ActionRepositoryEx;

@Service
@RequiredArgsConstructor
public class ActionRepositoryExImpl implements ActionRepositoryEx, MapperDbo {

  private final ActionRepository repository;

  @Override
  public Boolean save(ShardedId entityId, ActionValue entity) {
    var model = entity.withId(entityId);
    var dbModel = toDbo(model);
    repository.save(dbModel);
    return true;
  }


  @Override
  public ShardedId update(Entity<ActionValue> entity) {
    var desired = entity;
    var template = toDbo(desired);
    var id = entity.getId();
    var actual = repository.findByProjectidEntityidEntityversion(id.getProjectId(), id.getId(), id.getVersion());
    actual.setServicemanEmail(template.getServicemanEmail());
    actual.setDescription(template.getDescription());
    actual.setDistance(template.getDistance());
    actual.setDuration(template.getDuration());
    actual.setServicemanName(template.getServicemanName());
    actual.setDate(template.getDate());
    actual.setCustomerId(template.getCustomerId());
    return id.next();
  }

  @Override
  public Boolean remove(ShardedId id) {
    var projectId = id.getProjectId();
    var entityId = id.getId();
    var version = id.getVersion();
    repository.deleteByProjectidEntityidEntityversion(projectId, entityId, version);
    return true;
  }
}

