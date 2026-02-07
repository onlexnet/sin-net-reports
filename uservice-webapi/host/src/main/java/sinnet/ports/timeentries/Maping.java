package sinnet.ports.timeentries;

import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import sinnet.gql.api.CommonMapper;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.ServiceModelGql;
import sinnet.grpc.timeentries.TimeEntryModel;

@Component
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class Maping {
  
  private final CommonMapper commonMapper;

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
        .setWhenProvided(commonMapper.fromGrpc(model.getWhenProvided()));
  }


}
