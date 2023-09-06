package sinnet.gql.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.domain.EntityId;
import sinnet.gql.models.ServiceEntryInputGql;
import sinnet.grpc.ActionsGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsMutationUpdate {
  
  private final ActionsGrpcFacade actionsGrpc;

  @SchemaMapping 
  boolean update(ActionsMutation self, ServiceEntryInputGql content, String entityId, int entityVersion) {
    var requestorEmail = self.userToken().getRequestorEmail();
    var projectId = UUID.fromString(self.userToken().getProjectId());

    return actionsGrpc.update(
        new EntityId(projectId, UUID.fromString(entityId), entityVersion),
        content.getCustomerId(),
        content.getDescription(),
        Optional.ofNullable(content.getDistance()).orElse(0),
        Optional.ofNullable(content.getDuration()).orElse(0),
        content.getServicemanName(),
        content.getServicemanName(),
        content.getWhenProvided());
  }
}
