package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;

@Controller
@RequiredArgsConstructor
class ActionsMutationRemove {
  
  private final TimeentriesServicePortIn actionsGrpc;

  @SchemaMapping 
  Boolean remove(ActionsMutation self, @Argument String entityId, @Argument int entityVersion) {
    var entityIdTyped = UUID.fromString(entityId);
    var projectId = self.userToken().projectId();

    return actionsGrpc.remove(projectId, entityIdTyped, entityVersion);
  }
}
