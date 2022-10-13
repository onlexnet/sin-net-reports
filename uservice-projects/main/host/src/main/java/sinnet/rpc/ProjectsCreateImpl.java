package sinnet.rpc;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.access.AccessFacade;
import sinnet.dbo.DboCreate;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.CreateReply;
import sinnet.grpc.projects.CreateRequest;
import sinnet.grpc.projects.ProjectId;
import sinnet.model.ValProjectId;
import sinnet.project.events.ProjectCreatedEvent;
import sinnet.model.ValEmail;

@ApplicationScoped
@RequiredArgsConstructor
class ProjectsCreateImpl implements ProjectsCreate {

  private final AccessFacade accessFacade;
  private final DboFacade dboFacade;
  private final Event<ProjectCreatedEvent> onProjectCreated;

  public Uni<CreateReply> create(CreateRequest request) {
    var requestor = request.getUserToken();

    var valId = ValProjectId.of(UUID.randomUUID());
    var emailOfRequestor = ValEmail.of(requestor.getRequestorEmail());
    return accessFacade.guardAccess(requestor, valId, roleContext -> roleContext::canCreateProject)
      .chain(id -> dboFacade.create(new DboCreate.CreateContent(valId, emailOfRequestor)))
      .map(it -> {
        if (it instanceof DboCreate.Success x) {

          var evt = ProjectCreatedEvent.newBuilder()
              .setEid(x.getVid().id().toString())
              .setEtag(x.getVid().tag())
              .build();
          onProjectCreated.fire(evt);

          return CreateReply.newBuilder()
            .setEntityId(ProjectId.newBuilder()
            .setEId(valId.value().toString())
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
