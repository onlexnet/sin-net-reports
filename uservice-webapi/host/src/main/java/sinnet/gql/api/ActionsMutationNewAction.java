package sinnet.gql.api;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.SomeEntityGql;
import sinnet.ports.timeentries.ActionsGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsMutationNewAction {
  
  private final ActionsGrpcFacade actionsGrpc;

  @SchemaMapping 
  SomeEntityGql newAction(ActionsMutation self) {
    var requestorEmail = self.userToken().getRequestorEmail();
    var projectId = UUID.fromString(self.userToken().getProjectId());

    // no security check as each person can create its own projects

    var createId = actionsGrpc.newAction(requestorEmail, projectId, LocalDate.now());

    return new SomeEntityGql()
        .setEntityId(createId.id().toString())
        .setEntityVersion(createId.tag())
        .setProjectId(createId.projectId().toString());
  }
}
