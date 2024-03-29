package sinnet.grpc.common;

import java.time.YearMonth;
import java.util.UUID;

import sinnet.domain.model.ValEmail;
import sinnet.grpc.mapping.PropsBuilder;
import sinnet.models.ShardedId;
import sinnet.models.UserToken;

/**
 * TBD.
 */
public interface Mapper {

  /**
   * TBD.
   */
  static ShardedId fromDto(sinnet.grpc.common.EntityId it) {
    if (it == null) {
      return null;
    }
    var projectId = UUID.fromString(it.getProjectId());
    var entityId = UUID.fromString(it.getEntityId());
    var entityVersion = it.getEntityVersion();
    return ShardedId.of(projectId, entityId, entityVersion);
  }

  /**
   * TBD.
   */
  default UserToken fromDto(sinnet.grpc.common.UserToken dtoModel) {
    var emailAsString = dtoModel.getRequestorEmail();
    var email = ValEmail.of(emailAsString);
    return new UserToken(email);
  }

  /**
   * TBD.
   */
  static YearMonth fromDto(sinnet.reports.grpc.YearMonth dto) {
    return YearMonth.of(dto.getYear(), dto.getMonth());
  }

  /**
   * TBD.
   */
  static sinnet.grpc.common.EntityId toDto(ShardedId eid) {
    if (eid == null) {
      return null;
    }
    return PropsBuilder.build(sinnet.grpc.common.EntityId.newBuilder())
        .set(eid.getProjectId(), UUID::toString, b -> b::setProjectId)
        .set(eid.getId(), UUID::toString, b -> b::setEntityId)
        .set(eid.getVersion(), b -> b::setEntityVersion)
        .done().build();
  }

}
