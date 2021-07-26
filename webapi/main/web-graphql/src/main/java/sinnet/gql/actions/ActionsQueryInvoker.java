package sinnet.gql.actions;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sinnet.ActionRepository;

@Component
public class ActionsQueryInvoker {

  @Autowired
  private ActionRepository actionRepository;

  public CompletionStage<ServiceModel> find(UUID projectId, UUID actionId) {
    return actionRepository.find(projectId, actionId)
      .map(actionEntity -> ServiceModel.builder()
        .projectId(actionEntity.getProjectId())
        .entityId(actionEntity.getEntityId())
        .entityVersion(actionEntity.getVersion())
        .servicemanName(actionEntity.getValue().getWho().getValue())
        .whenProvided(actionEntity.getValue().getWhen())
        .description(actionEntity.getValue().getWhat())
        .duration(actionEntity.getValue().getHowLong().getValue())
        .distance(actionEntity.getValue().getHowFar().getValue())
        .build())
      .toCompletionStage();
  }
}
