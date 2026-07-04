package sinnet.infra.adapters.gql;

import java.time.LocalDate;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.TimeentriesServicePortIn;
import sinnet.gql.models.SomeEntityGql;

@Controller
@RequiredArgsConstructor
class ActionsMutationNewAction {
  
  private final TimeentriesServicePortIn actionsGrpc;

  @SchemaMapping 
  SomeEntityGql newAction(ActionsMutation self) {
    var requestorEmail = self.userToken().requestorEmail();
    var projectId = self.userToken().projectId();

    // no security check as each person can create its own projects

    var createId = actionsGrpc.newEntry(requestorEmail, projectId, LocalDate.now());

    return new SomeEntityGql()
        .setEntityId(createId.id().toString())
        .setEntityVersion(createId.tag())
        .setProjectId(createId.projectId().toString());
  }
}
