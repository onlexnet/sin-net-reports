package sinnet.infra.adapters.grpc;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;
import sinnet.grpc.timeentries.TimeEntryModel;

import java.time.LocalDate;

/** TBD. */
@Mapper
public interface TimeEntryModelMapper {

  static TimeEntryModelMapper apply = Mappers.getMapper(TimeEntryModelMapper.class);

  @org.mapstruct.Mapping(source = "entityId", target = "id")
  @org.mapstruct.Mapping(source = "entityVersion", target = "tag")
  EntityId fromDto(sinnet.grpc.common.EntityId it);

  TimeEntry fromDto(TimeEntryModel it);

  /** TBD. */
  default LocalDate map(sinnet.grpc.timeentries.LocalDate value) {
    if (value.getMonth() == 0) {
      return null;
    }
    return LocalDate.of(value.getYear(), value.getMonth(), value.getDay());
  }

}
