package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.ActionsGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsMutationRemove {
  
  private final ActionsGrpcFacade actionsGrpc;

  @SchemaMapping 
  Boolean remove(ActionsMutation self, @Argument String entityId, @Argument int entityVersion) {
    var entityIdTyped = UUID.fromString(entityId);
    var projectId = UUID.fromString(self.userToken().getProjectId());

    return actionsGrpc.remove(projectId, entityIdTyped, entityVersion);
  }
}
