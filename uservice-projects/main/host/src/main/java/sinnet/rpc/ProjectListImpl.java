package sinnet.rpc;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.ListReply;
import sinnet.grpc.projects.ListRequest;
import sinnet.model.Email;

@ApplicationScoped
class ProjectsListImpl implements ProjectList {

  @Inject
  DboFacade dbo;

  @Override
  @ReactiveTransactional
  public Uni<ListReply> list(ListRequest request) {
    var emailOfRequestor = Email.of(request.getEmailOfRequestor());
    return dbo.ownedAsProject(emailOfRequestor)
      .map(it -> ListReply.newBuilder().addAllProjects(it).build());
  }

}
