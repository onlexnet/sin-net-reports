package sinnet.grpc.projects;

import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.generated.ListReply;
import sinnet.grpc.projects.generated.ListRequest;
import sinnet.domain.model.ValEmail;

@Component
@RequiredArgsConstructor
class ProjectListImpl implements RpcQueryHandler<ListRequest, ListReply> {

  private final DboFacade dbo;

  @Override
  public ListReply apply(ListRequest request) {
    var emailOfRequestor = ValEmail.of(request.getEmailOfRequestor());

    var ownedProjects = dbo.ownedAsId(emailOfRequestor).toJavaStream();
    var associadedProjects = dbo.assignedAsId(emailOfRequestor).toJavaStream();

    var availableProjects = Stream.concat(ownedProjects, associadedProjects).collect(Collectors.toSet());

    // return ListReply.newBuilder().addAllProjects(ownedProjects).build();
    throw new IllegalStateException();
  }

}
