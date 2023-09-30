package sinnet.grpc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import io.micrometer.common.util.StringUtils;
import sinnet.domain.EntityId;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.grpc.timeentries.TimeEntryModel;

/** GRPC methods related to Actions (aka Services aka Timeentries). */
public interface ActionsGrpcFacade {

  /** REturns list of actions for requested project, limited result from-to range. */
  List<TimeEntryModel> searchInternal(UUID projectId, LocalDate from, LocalDate to);

  /** Fixme. */
  EntityId newAction(String requestorEmail, UUID projectId, LocalDate when);

  /** Fixme. */
  boolean update(EntityId entityId, String customerId, String description, int distance, int duration,
                 String servicemanEmail, String servicemanName, LocalDate whenProvided);
  
  /** Fixme. */
  TimeEntryModel getActionInternal(UUID projectId, UUID entityId);

  default ServiceModelGql getAction(UUID projectId, UUID entityId, Function<String, CustomerEntityGql> customerMapper) {
    var dto =  getActionInternal(projectId, entityId);
    return map(dto, customerMapper);
  }
  
  /** Returns list of actions for requested project, limited result from-to range. */
  default List<ServiceModelGql> search(UUID projectId, LocalDate from, LocalDate to, Function<String, CustomerEntityGql> customerMapper) {
    return searchInternal(projectId, from, to).stream().map(it -> map(it, customerMapper)).toList();
  }

  /** Internal mapping. */
  static ServiceModelGql map(TimeEntryModel model, Function<String, CustomerEntityGql> customerMapper) {
    var customerId = model.getCustomerId();
    var customer = StringUtils.isEmpty(customerId)
        ? null
        : customerMapper.apply(customerId);

    return new ServiceModelGql()
        .setCustomer(customer)
        .setDescription(model.getDescription())
        .setDistance(model.getDistance())
        .setDuration(model.getDuration())
        .setEntityId(model.getEntityId().getEntityId())
        .setEntityVersion(model.getEntityId().getEntityVersion())
        .setProjectId(model.getEntityId().getProjectId())
        .setServicemanEmail(model.getServicemanEmail())
        .setServicemanName(model.getServicemanName())
        .setWhenProvided(CommonMapper.fromGrpc(model.getWhenProvided()));
  }
}
