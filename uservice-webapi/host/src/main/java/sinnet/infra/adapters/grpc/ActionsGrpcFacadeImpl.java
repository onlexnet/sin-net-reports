package sinnet.infra.adapters.grpc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;
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
class ActionsGrpcFacadeImpl implements ActionsGrpcPortOut {

  private final TimeEntriesBlockingStub stub;
  private final EntityGrpcMapper entityGrpcMapper = EntityGrpcMapper.INSTANCE;


  @Override
  public List<TimeEntry> searchInternal(UUID projectId, LocalDate from, LocalDate to) {

    var searchQuery = SearchQuery.newBuilder()
        .setFrom(entityGrpcMapper.toGrpc(from))
        .setTo(entityGrpcMapper.toGrpc(to))
        .setProjectId(projectId.toString())
        .build();

    var dto = stub.search(searchQuery);
    var result = dto.getActivitiesList();

    return result.stream().map(TimeEntryModelMapper.apply::fromDto).toList();
  }

  @Override
  public EntityId newAction(String requestorEmail, UUID projectId, LocalDate when) {

    var cmd = ReserveCommand.newBuilder()
        .setInvoker(UserToken.newBuilder()
          .setProjectId(projectId.toString())
          .setRequestorEmail(requestorEmail))
        .setWhen(entityGrpcMapper.toGrpc(when))
        .build();

    var result = stub.reserve(cmd);

    return entityGrpcMapper.fromGrpc(result.getEntityId());
  }
  
  @Override
  public TimeEntry getActionInternal(UUID projectId, UUID timeentryId) {

    var query = GetQuery.newBuilder()
        .setProjectId(projectId.toString())
        .setTimeentryId(timeentryId.toString())
        .build();

    var result = stub.get(query);
    var item = result.getItem();

    return TimeEntryModelMapper.apply.fromDto(item);
  }

  @Override
  public boolean update(EntityId entitId, String customerId, String description, int distance, int duration,
                        String servicemanEmail, String servicemanName, LocalDate whenProvided) {
    var whenProvidedGprc = entityGrpcMapper.toGrpc(whenProvided);
    var entitIdGrpc = entityGrpcMapper.toGrpc(entitId);
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

