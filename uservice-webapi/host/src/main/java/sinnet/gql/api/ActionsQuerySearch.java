package sinnet.gql.api;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.ServiceFilterInputGql;
import sinnet.gql.models.ServicesSearchResultGql;
import sinnet.grpc.ActionsGrpcFacade;

@Controller
@RequiredArgsConstructor
class ActionsQuerySearch {

  private final ActionsGrpcFacade service;

  @SchemaMapping
  ServicesSearchResultGql search(ActionsQuery self, @Argument ServiceFilterInputGql filter) {
    var requestorEmail = self.primaryEmail();
    var projectId = UUID.fromString(self.projectId());
    var result = service.search(projectId, filter.getFrom(), filter.getTo());
    return new ServicesSearchResultGql()
      .setItems(result);
  }

}


