package sinnet.domain.serviceman;

import org.jmolecules.ddd.annotation.Entity;

import lombok.Value;
import sinnet.models.Email;
import sinnet.models.EntityId;

@Entity
@Value
class ServicemanState {
  private EntityId entityId;
  private Email email;
  private String name;
}
