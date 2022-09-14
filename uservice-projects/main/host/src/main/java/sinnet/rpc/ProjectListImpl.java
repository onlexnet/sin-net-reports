package sinnet.rpc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.smallrye.mutiny.Uni;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.model.ValEmail;

@ApplicationScoped
class ProjectsListImpl implements ProjectList {

  @Inject
  DboFacade dbo;

  @Override
  public Uni<ListReply> list(ListRequest request) {
    var emailOfRequestor = ValEmail.of(request.getEmailOfRequestor());
    return dbo.ownedAsProject(emailOfRequestor)
      .map(it -> ListReply.newBuilder().addAllProjects(it).build());
  }

}
