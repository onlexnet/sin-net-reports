package sinnet.ports.timeentries;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import io.micrometer.common.util.StringUtils;
import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;

/** GRPC methods related to Actions (aka Services aka Timeentries). */
public interface ActionsGrpcFacade {

  /** REturns list of actions for requested project, limited result from-to range. */
  List<TimeEntry> searchInternal(UUID projectId, LocalDate from, LocalDate to);

  /** Fixme. */
  EntityId newAction(String requestorEmail, UUID projectId, LocalDate when);

  /** Fixme. */
  boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
                 String servicemanEmail, String servicemanName, LocalDate whenProvided);
  
  /** Doxme. */       
  boolean remove(UUID projectId, UUID entityId, int entityVersion);

  /** Fixme. */
  TimeEntry getActionInternal(UUID projectId, UUID entityId);

  default ServiceModelGql getAction(UUID projectId, UUID entityId, Function<String, CustomerEntityGql> customerMapper,
                                   CommonMapper commonMapper) {
    var dto =  getActionInternal(projectId, entityId);
    return mapWithMapper(dto, customerMapper, commonMapper);
  }
  
  /** Returns list of actions for requested project, limited result from-to range. */
  default List<ServiceModelGql> search(UUID projectId, LocalDate from, LocalDate to, Function<String, CustomerEntityGql> customerMapper,
                                      CommonMapper commonMapper) {
    return searchInternal(projectId, from, to).stream().map(it -> mapWithMapper(it, customerMapper, commonMapper)).toList();
  }

  /** Internal mapping - requires CommonMapper instance. */
  default ServiceModelGql mapWithMapper(TimeEntry model, Function<String, CustomerEntityGql> customerMapper,
                                       CommonMapper commonMapper) {
    var customerId = model.customerId();
    var customer = StringUtils.isEmpty(customerId)
        ? null
        : customerMapper.apply(customerId);

    return new ServiceModelGql()
        .setCustomer(customer)
        .setDescription(model.description())
        .setDistance(model.distance())
        .setDuration(model.duration())
        .setEntityId(model.entityId().id().toString())
        .setEntityVersion(model.entityId().tag())
        .setProjectId(model.entityId().projectId().toString())
        .setServicemanEmail(model.servicemanEmail())
        .setServicemanName(model.servicemanName())
        .setWhenProvided(model.whenProvided());
  }
}
