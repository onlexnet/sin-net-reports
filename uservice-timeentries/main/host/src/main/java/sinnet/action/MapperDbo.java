package sinnet.action;

import sinnet.action.ActionRepository.ActionDbo;
import sinnet.models.ActionValue;
import sinnet.models.Entity;

public interface MapperDbo {

  default ActionDbo toDbo(Entity<ActionValue> entity) {
    var value = entity.getValue();
    return new ActionDbo()
      .setProjectId(entity.getProjectId())
      .setEntityId(entity.getEntityId())
      .setEntityVersion(entity.getVersion())
      .setServicemanEmail(value.getWho().getValue())
      .setDescription(value.getWhat())
      .setDistance(value.getHowFar().getValue())
      .setDuration(value.getHowLong().getValue())
      .setServicemanName(value.getWho().getValue())
      .setDate(value.getWhen())
      // TODO untained customer id
      .setCustomerId(value.getWhom());

  }
}
