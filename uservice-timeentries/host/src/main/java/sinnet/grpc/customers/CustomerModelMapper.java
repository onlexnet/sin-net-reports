package sinnet.grpc.customers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.EntityId;
import sinnet.models.ShardedId;
import sinnet.models.ValName;

/** CustomerModel maper DTO <-> PROTO. */
@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = { UuidMapper.class }
)
public interface CustomerModelMapper {
  
  CustomerModelMapper INSTANCE = Mappers.getMapper(CustomerModelMapper.class);

  @Mapping(target = "secretsEx", source = "secretEx")
  sinnet.models.CustomerModel fromDto(sinnet.grpc.customers.CustomerModel dto);

  default ValEmail fromDto(String dto) {
    return ValEmail.of(dto);
  }

  default LocalDateTime fromDto(sinnet.grpc.customers.LocalDateTime dto) {
    return LocalDateTime.of(dto.getYear(), dto.getMonth(), dto.getDay(), dto.getHour(), dto.getMinute(), dto.getSecond());
  }

  @Mapping(target = "id", source = "entityId")
  @Mapping(target = "version", source = "entityVersion")
  ShardedId fromDto(EntityId dto);

  default ValName fromDto2(String dto) {
    return ValName.of(dto);
  }

  default String map(ValEmail source) {
    return Optional.ofNullable(source.value()).orElse("");
  }

  default String map2(ValName source) {
    return Optional.ofNullable(source.getValue()).orElse("");
  }

  @Mapping(target = "secretEx", source = "secretsEx")
  sinnet.grpc.customers.CustomerModel toDto(sinnet.models.CustomerModel dto);

  sinnet.grpc.customers.CustomerValue toDto(sinnet.models.CustomerValue dto);

  /** Mapper. */
  default sinnet.grpc.customers.LocalDateTime toDto(LocalDateTime dto) {
    return sinnet.grpc.customers.LocalDateTime.newBuilder()
        .setYear(dto.getYear())
        .setMonth(dto.getMonthValue())
        .setDay(dto.getDayOfMonth())
        .setHour(dto.getHour())
        .setMinute(dto.getMinute())
        .setSecond(dto.getSecond())
        .build();
  }

  @Mapping(target = "entityId", source = "id")
  @Mapping(target = "entityVersion", source = "version")
  EntityId toDto(ShardedId source);

  @Mapping(target = "secretEx", source = "secretsEx")
  List<sinnet.grpc.customers.CustomerContact> toDtoContacts(List<sinnet.models.CustomerContact> model);

}
 