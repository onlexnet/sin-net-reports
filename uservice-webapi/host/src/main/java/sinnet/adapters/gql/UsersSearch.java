package sinnet.adapters.gql;


import java.util.List;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.UsersSearchQuery;
import sinnet.app.ports.in.UsersSearchPortIn;
import sinnet.gql.models.UserGql;

@Controller
@RequiredArgsConstructor
class UsersSearch {

  private final UsersSearchPortIn service;

  @SchemaMapping
  List<UserGql> search(UsersQuery self) {
    var query = new UsersSearchQuery(self.projectId(), self.primaryEmail());

    var result = service.search(query);
    return Iterator.ofAll(result.items())
        .map(it -> new UserGql(it.email(), it.entityId()))
        .toJavaList();
  }

}

