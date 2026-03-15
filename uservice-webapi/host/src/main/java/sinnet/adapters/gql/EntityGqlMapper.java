package sinnet.adapters.gql;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.gql.models.EntityGql;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface EntityGqlMapper {

  EntityGqlMapper INSTANCE = Mappers.getMapper(EntityGqlMapper.class);

  @Mapping(target = "projectId", source = "projectId")
  @Mapping(target = "entityId", source = "id")
  @Mapping(target = "entityVersion", source = "tag")
  EntityGql toGql(EntityId source);

  @Mapping(target = "projectId", source = "projectId")
  @Mapping(target = "id", source = "entityId")
  @Mapping(target = "tag", source = "entityVersion")
  EntityId toDomain(EntityGql source);

  default UUID toUuid(String value) {
    return value == null || value.isEmpty() ? null : UUID.fromString(value);
  }

  default String toStringOrNull(UUID value) {
    return value == null ? null : value.toString();
  }
}
