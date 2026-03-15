package sinnet.infra.adapters.grpc;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.grpc.customers.LocalDateTime;
import sinnet.grpc.timeentries.LocalDate;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.ERROR)
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

  default LocalDate toGrpc(java.time.LocalDate source) {
    return LocalDate.newBuilder()
      .setYear(source.getYear())
      .setMonth(source.getMonthValue())
      .setDay(source.getDayOfMonth())
      .build();
  }

  default LocalDateTime toGrpc(java.time.LocalDateTime source) {
    return LocalDateTime.newBuilder()
      .setYear(source.getYear())
      .setMonth(source.getMonthValue())
      .setDay(source.getDayOfMonth())
      .setHour(source.getHour())
      .setMinute(source.getMinute())
      .setSecond(source.getSecond())
      .build();
  }

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
