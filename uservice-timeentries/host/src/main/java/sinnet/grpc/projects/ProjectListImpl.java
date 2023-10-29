package sinnet.grpc.projects;

import java.util.stream.Stream;
import java.util.stream.Collectors;

import io.vavr.collection.Iterator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.generated.ListReply;
import sinnet.grpc.projects.generated.ListRequest;
import sinnet.domain.model.ValEmail;

@Component
@RequiredArgsConstructor
@Slf4j
class ProjectListImpl implements RpcQueryHandler<ListRequest, ListReply> {

  private final DboFacade dbo;

  @Override
  public ListReply apply(ListRequest request) {
    var emailOfRequestor = ValEmail.of(request.getEmailOfRequestor());

    var ownedProjects = dbo.ownedAsId(emailOfRequestor).toJavaStream();
    var associadedProjects = dbo.assignedAsId(emailOfRequestor).toJavaStream();

    var availableProjects = Stream.concat(ownedProjects, associadedProjects).collect(Collectors.toSet());
    var projects = dbo.getAll(Iterator.ofAll(availableProjects));

    return ListReply.newBuilder().addAllProjects(projects).build();
  }

}
