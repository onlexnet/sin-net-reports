package sinnet.infra.adapters.grpc;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.grpc.customers.LocalDateTime;
import sinnet.grpc.timeentries.LocalDate;

@Mapper
public interface EntityGrpcMapper {

  EntityGrpcMapper INSTANCE = Mappers.getMapper(EntityGrpcMapper.class);

  @Mapping(target = "projectId", source = "projectId")
  @Mapping(target = "entityId", source = "id")
  @Mapping(target = "entityVersion", source = "tag")
  sinnet.grpc.common.EntityId toGrpc(EntityId source);

  @Mapping(target = "projectId", source = "projectId")
  @Mapping(target = "id", source = "entityId")
  @Mapping(target = "tag", source = "entityVersion")
  EntityId fromGrpc(sinnet.grpc.common.EntityId source);

  @Mapping(target = "year", source = "year")
  @Mapping(target = "month", source = "monthValue")
  @Mapping(target = "day", source = "dayOfMonth")
  LocalDate toGrpc(java.time.LocalDate source);

  @Mapping(target = "year", source = "year")
  @Mapping(target = "month", source = "monthValue")
  @Mapping(target = "day", source = "dayOfMonth")
  @Mapping(target = "hour", source = "hour")
  @Mapping(target = "minute", source = "minute")
  LocalDateTime toGrpc(java.time.LocalDateTime source);

  default java.time.LocalDate fromGrpc(LocalDate source) {
    if (source == null) {
      return null;
    }
    return java.time.LocalDate.of(source.getYear(), source.getMonth(), source.getDay());
  }

  default UUID toUuid(String value) {
    return value == null || value.isEmpty() ? null : UUID.fromString(value);
  }

  default String toStringOrNull(UUID value) {
    return value == null ? null : value.toString();
  }
}
