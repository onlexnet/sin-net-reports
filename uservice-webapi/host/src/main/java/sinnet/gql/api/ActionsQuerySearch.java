package sinnet.gql.api;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import sinnet.gql.models.ServicesSearchResultGql;

@Controller
@RequiredArgsConstructor
class ActionsQuerySearch {

  // private final ProjectsGrpcFacade service;

  @SchemaMapping
  ServicesSearchResultGql search(ActionsQuery self, @Argument String name) {
    var requestorEmail = self.primaryEmail();
    // var result = service.list(requestorEmail, ProjectsMapper::toDto);
    return new ServicesSearchResultGql()
      .setItems(List.of());
  }

}


