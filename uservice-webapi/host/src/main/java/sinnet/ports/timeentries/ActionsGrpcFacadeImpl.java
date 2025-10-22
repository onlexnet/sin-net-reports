package sinnet.ports.timeentries;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.domain.EntityId;
import sinnet.gql.api.CommonMapper;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.timeentries.GetQuery;
import sinnet.grpc.timeentries.RemoveCommand;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.TimeEntriesGrpc.TimeEntriesBlockingStub;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.grpc.timeentries.UpdateCommand;


/** Mockable equivalent of {@link ProjectsGrpcStub}. */
@Component
@RequiredArgsConstructor
class ActionsGrpcFacadeImpl implements ActionsGrpcFacade {

  private final TimeEntriesBlockingStub stub;

  @Override
  public List<TimeEntryModel> searchInternal(UUID projectId, LocalDate from, LocalDate to) {

    var searchQuery = SearchQuery.newBuilder()
        .setFrom(CommonMapper.toGrpc(from))
        .setTo(CommonMapper.toGrpc(to))
        .setProjectId(projectId.toString())
        .build();

    var result = stub.search(searchQuery);

    return result.getActivitiesList();
  }

  @Override
  public EntityId newAction(String requestorEmail, UUID projectId, LocalDate when) {

    var cmd = ReserveCommand.newBuilder()
        .setInvoker(UserToken.newBuilder()
          .setProjectId(projectId.toString())
          .setRequestorEmail(requestorEmail))
        .setWhen(CommonMapper.toGrpc(when))
        .build();

    var result = stub.reserve(cmd);

    return CommonMapper.fromGrpc(result.getEntityId());
  }
  
  @Override
  public TimeEntryModel getActionInternal(UUID projectId, UUID timeentryId) {

    var query = GetQuery.newBuilder()
        .setProjectId(projectId.toString())
        .setTimeentryId(timeentryId.toString())
        .build();

    var result = stub.get(query);

    return result.getItem();
  }

  @Override
  public boolean update(EntityId entitId, String customerId, String description, int distance, int duration,
                        String servicemanEmail, String servicemanName, LocalDate whenProvided) {
    var whenProvidedGprc = CommonMapper.toGrpc(whenProvided);
    var entitIdGrpc = CommonMapper.toGrpc(entitId);
    var model = TimeEntryModel.newBuilder()
        .setCustomerId(customerId)
        .setDescription(description)
        .setDistance(distance)
        .setDuration(duration)
        .setServicemanEmail(servicemanEmail)
        .setServicemanName(servicemanName)
        .setWhenProvided(whenProvidedGprc)
        .setEntityId(entitIdGrpc)
        .build();
    var cmd = UpdateCommand.newBuilder()
        .setModel(model)
        .build();
        

    var result = stub.update(cmd);

    return result.getSuccess();
  }

  @Override
  public boolean remove(UUID projectId, UUID entityId, int entityVersion) {
    var removeCommand = RemoveCommand.newBuilder()
        .setEntityId(sinnet.grpc.common.EntityId.newBuilder()
            .setEntityId(entityId.toString())
            .setProjectId(projectId.toString())
            .setEntityVersion(entityVersion)
            .build())
        .build();
        
    var result = stub.remove(removeCommand);

    return result.getResult();
  }

}
