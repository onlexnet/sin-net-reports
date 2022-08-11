package sinnet.rpc;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Validator;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.grpc.projects.ProjectId;
import sinnet.model.Email;
import sinnet.model.ProjectIdHolder;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboCreate;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.CreateReply;
import sinnet.grpc.projects.CreateRequest;

@ApplicationScoped
@RequiredArgsConstructor
class ProjectsCreateImpl implements ProjectsCreate {

  private final AccessFacade accessFacade;
  private final DboFacade dboFacade;
  
  public Uni<CreateReply> create(CreateRequest request) {
    var requestor = request.getUserToken();

    var idHolder = ProjectIdHolder.of(UUID.randomUUID());
    var emailOfRequestor = Email.of(requestor.getRequestorEmail());
    return accessFacade.guardAccess(requestor, idHolder, roleContext -> roleContext::canCreateProject)
      .chain(id -> dboFacade.create(idHolder, emailOfRequestor))
      .map(it -> {
        if (it instanceof DboCreate.Success x) {
          return CreateReply.newBuilder()
            .setEntityId(ProjectId.newBuilder()
            .setEId(idHolder.value().toString())
            .setETag(1))
            .build();
        } else if (it instanceof DboCreate.ValidationFailed x) {
          throw Status.FAILED_PRECONDITION.withDescription(x.getReason()).asRuntimeException();
        } else {
          throw Status.INTERNAL.asRuntimeException();
        }
      });
  }

}
