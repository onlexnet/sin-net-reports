package sinnet.gql.api;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sinnet.domain.EntityId;
import sinnet.gql.models.EntityGql;
import sinnet.grpc.customers.LocalDateTime;
import sinnet.grpc.projects.generated.ProjectId;
import sinnet.grpc.timeentries.LocalDate;

/** MapStruct mapper for common conversions between gRPC, GraphQL, and domain models. */
@Mapper(componentModel = "spring")
public interface CommonMapper {

  /** Grpc -> Gql mapping. */
  @Mapping(target = "projectId", source = "EId")
  @Mapping(target = "entityId", source = "EId")
  @Mapping(target = "entityVersion", source = "ETag")
  EntityGql toGql(ProjectId source);

  /** Grpc -> Gql mapping. */
  @Mapping(target = "projectId", source = "projectId")
  @Mapping(target = "entityId", source = "entityId")
  @Mapping(target = "entityVersion", source = "entityVersion")
  EntityGql toGql(sinnet.grpc.common.EntityId source);

  /** Gql -> Grpc mapping. */
  @Mapping(target = "EId", source = "entityId")
  @Mapping(target = "ETag", source = "entityVersion")
  ProjectId toGrpc(EntityGql source);

  /** Domain -> Grpc mapping. */
  @Mapping(target = "projectId", expression = "java(source.projectId().toString())")
  @Mapping(target = "entityId", expression = "java(source.id().toString())")
  @Mapping(target = "entityVersion", source = "tag")
  sinnet.grpc.common.EntityId toGrpc(EntityId source);

  /** LocalDate conversion. */
  @Mapping(target = "year", source = "year")
  @Mapping(target = "month", source = "monthValue")
  @Mapping(target = "day", source = "dayOfMonth")
  LocalDate toGrpc(java.time.LocalDate source);

  /** LocalDateTime conversion. */
  @Mapping(target = "year", source = "year")
  @Mapping(target = "month", source = "monthValue")
  @Mapping(target = "day", source = "dayOfMonth")
  @Mapping(target = "hour", source = "hour")
  @Mapping(target = "minute", source = "minute")
  LocalDateTime toGrpc(java.time.LocalDateTime source);

  /** Grpc -> Domain mapping. */
  default EntityId fromGrpc(sinnet.grpc.common.EntityId dto) {
    if (dto == null) {
      return null;
    }
    var projectId = UUID.fromString(dto.getProjectId());
    var entityId = UUID.fromString(dto.getEntityId());
    var entityTag = dto.getEntityVersion();
    return new EntityId(projectId, entityId, entityTag);
  }

  /** LocalDate conversion. */
  default java.time.LocalDate fromGrpc(LocalDate whenProvided) {
    if (whenProvided == null) {
      return null;
    }
    return java.time.LocalDate.of(whenProvided.getYear(), whenProvided.getMonth(), whenProvided.getDay());
  }

}
