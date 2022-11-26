package sinnet.action;

import sinnet.action.ActionRepository.ActionDbo;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.ValEmail;
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

  default Entity<ActionValue> fromDbo(ActionDbo dbo) {
    return new ActionValue()
      .setWho(ValEmail.of(dbo.getServicemanEmail()))
      .setWhen(dbo.getDate())
      .setWhom(dbo.getCustomerId())
      .setWhat(dbo.getDescription())
      .setHowLong(ActionDuration.of(dbo.getDuration()))
      .setHowFar(Distance.of(dbo.getDistance()))
      .withId(dbo.getProjectId(), dbo.getEntityId(), dbo.getEntityVersion());
  }

}
