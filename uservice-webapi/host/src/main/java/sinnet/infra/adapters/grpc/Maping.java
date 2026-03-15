package sinnet.infra.adapters.grpc;

import org.springframework.stereotype.Component;

import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.grpc.timeentries.TimeEntryModel;

@Component
class Maping {

  private final EntityGrpcMapper entityGrpcMapper = EntityGrpcMapper.INSTANCE;

  ServiceModelGql map(TimeEntryModel model, CustomerEntityGql customer) {
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
        .setWhenProvided(entityGrpcMapper.fromGrpc(model.getWhenProvided()));
  }


}
