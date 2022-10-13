package sinnet.domain.serviceman;

import lombok.Value;
import sinnet.models.Email;
import sinnet.models.EntityId;

@Value
class ServicemanState {
  private EntityId entityId;
  private Email email;
  private String name;
}
