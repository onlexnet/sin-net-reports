package sinnet.domain.serviceman;

import lombok.Value;
import sinnet.models.Email;
import sinnet.models.ShardedId;

@Value
class ServicemanState {
  private ShardedId entityId;
  private Email email;
  private String name;
}
