package sinnet.ports.timeentries;

import lombok.experimental.UtilityClass;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.grpc.timeentries.TimeEntryModel;

@UtilityClass
class Maping {
  
  static ServiceModelGql map(TimeEntryModel model, CustomerEntityGql customer) {
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
