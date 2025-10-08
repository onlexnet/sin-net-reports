package sinnet.action;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.models.EntityVersion;
import sinnet.models.ShardedId;
import sinnet.write.ActionRepositoryEx;

/**
 * TBD.
 */
@Service
@RequiredArgsConstructor
public class ActionRepositoryExImpl implements ActionRepositoryEx, MapperDbo {

  private final ActionRepository repository;

  @Override
  public ShardedId save(UUID projectId, UUID id, ActionValue entity) {
    var entityId = new ShardedId(projectId, id, null);
    var model = entity.withId(entityId);
    var dbModel = toDbo(model);
    repository.save(dbModel);
    return entityId.next();
  }


  @Override
  public ShardedId update1(Entity<ActionValue> entity) {
    var desired = entity;
    var template = toDbo(desired);
    var id = entity.getId();
    var actual = repository.findByProjectIdAndEntityIdAndEntityVersion(id.projectId(), id.id(), EntityVersion.toDbo(id.version()));
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
  public Boolean remove1(ShardedId id) {
    var projectId = id.projectId();
    var entityId = id.id();
    var version = EntityVersion.toDbo(id.version());
    repository.deleteByProjectIdAndEntityIdAndEntityVersion(projectId, entityId, version);
    return true;
  }

  @Override
  public List<Entity<ActionValue>> list(UUID projectId, LocalDate from, LocalDate to) {
    var dboResult = repository.findByProjectIdAndDateGreaterThanEqualAndDateLessThanEqual(projectId, from, to);
    return dboResult.stream().map(this::fromDbo).toList();
  }

  @Override
  public Entity<ActionValue> get(UUID projectId, UUID eid) {
    var dbo = repository.findByProjectIdAndEntityId(projectId, eid);
    return fromDbo(dbo);
  }

}

