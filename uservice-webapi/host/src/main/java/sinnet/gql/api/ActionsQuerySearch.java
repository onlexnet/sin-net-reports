package sinnet.gql.api;

import java.util.UUID;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.Cleanup;
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
    @Cleanup
    var event = new ExampleEvent();
    
    event.begin();
    var requestorEmail = self.primaryEmail();
    var projectId = UUID.fromString(self.projectId());
    var result = service.search(projectId, filter.getFrom(), filter.getTo());
    return new ServicesSearchResultGql()
      .setItems(result);
  }

  static class ExampleEvent extends jdk.jfr.Event implements AutoCloseable {

    public ExampleEvent() {
      super.begin();
    }

    @Override
    public void close() {
      super.end();
      super.commit();
    }
  }
}

