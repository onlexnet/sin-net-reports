package sinnet.gql.api;


import java.util.List;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import io.vavr.collection.Iterator;
import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.UsersSearchPortIn;
import sinnet.gql.models.UserGql;

@Controller
@RequiredArgsConstructor
class UsersSearch {

  private final UsersSearchPortIn service;

  @SchemaMapping
  List<UserGql> search(UsersQuery self) {
    var projectId = self.projectId();
    var primaryEmail = self.primaryEmail();

    var items = service.search(projectId, primaryEmail);
    return Iterator.ofAll(items)
        .map(it -> new UserGql(it.email(), it.entityId()))
        .toJavaList();
  }

}

