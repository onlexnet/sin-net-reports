package sinnet.gql.api;

import java.util.UUID;

import sinnet.domain.EntityId;
import sinnet.gql.models.EntityGql;
import sinnet.gql.utils.PropsBuilder;
import sinnet.grpc.projects.generated.ProjectId;
import sinnet.grpc.timeentries.LocalDate;

/** Mappings. */
public interface CommonMapper {

  /** Grpc -> Gql mapping. */
  public static EntityGql toGql(ProjectId it) {
    if (it == null) {
      return null;
    }
    var result = new EntityGql();
    result.setProjectId(it.getEId());
    result.setEntityId(it.getEId());
    result.setEntityVersion(it.getETag());
    return result;
  }

  /** Grpc -> Gql mapping. */
  public static EntityGql toGql(sinnet.grpc.common.EntityId it) {
    if (it == null) {
      return null;
    }
    var result = new EntityGql();
    result.setProjectId(it.getProjectId());
    result.setEntityId(it.getEntityId());
    result.setEntityVersion(it.getEntityVersion());
    return result;
  }

  /** LocalDate conversion. */
  public static java.time.LocalDate toGql(LocalDate whenProvided) {
    if (whenProvided == null) {
      return null;
    }

    return java.time.LocalDate.of(whenProvided.getYear(), whenProvided.getMonth(), whenProvided.getDay());
  }


  /** Gql -> Grpc mapping. */
  public static ProjectId toGrpc(EntityGql it) {
    if (it == null) {
      return null;
    }
    return PropsBuilder.build(ProjectId.newBuilder())
        .set(b -> b::setEId, it.getEntityId())
        .set(b -> b::setETag, it.getEntityVersion())
        .done().build();
  }

  /** LocalDate conversion. */
  public static LocalDate toGrpc(java.time.LocalDate from) {
    if (from == null) {
      return null;
    }

    return LocalDate.newBuilder()
        .setYear(from.getYear())
        .setMonth(from.getMonthValue())
        .setDay(from.getDayOfMonth())
        .build();
  }

  /** Fixme. */
  public static EntityId fromGrpc(sinnet.grpc.common.EntityId dto) {
    var projectId = UUID.fromString(dto.getProjectId());
    var entityId = UUID.fromString(dto.getEntityId());
    var entityTag = dto.getEntityVersion();
    return new EntityId(projectId, entityId, entityTag);
  }

}
