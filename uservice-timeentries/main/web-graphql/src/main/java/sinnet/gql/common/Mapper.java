package sinnet.gql.common;

import java.util.UUID;

import sinnet.grpc.PropsBuilder;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.UserToken;

public interface Mapper {
    
  default EntityId fromDto(sinnet.grpc.common.EntityId it) {
    if (it == null) {
      return null;
    }
    var projectId = UUID.fromString(it.getProjectId());
    var entityId = UUID.fromString(it.getEntityId());
    var entityVersion = it.getEntityVersion();
    return EntityId.of(projectId, entityId, entityVersion);
  }

    default UserToken fromDto(sinnet.grpc.common.UserToken dtoModel){
        var emailAsString = dtoModel.getRequestorEmail();
        var email = Email.of(emailAsString);
        return new UserToken(email);
    }

    default sinnet.grpc.common.EntityId toDto(EntityId eid) {
        if (eid == null) return null;
        return PropsBuilder.build(sinnet.grpc.common.EntityId.newBuilder())
            .set(eid.getProjectId(), o -> o.toString(), b -> b::setProjectId)
            .set(eid.getId(), o -> o.toString(), b -> b::setEntityId)
            .set(eid.getVersion(), b -> b::setEntityVersion)
            .done().build();
    }

}
