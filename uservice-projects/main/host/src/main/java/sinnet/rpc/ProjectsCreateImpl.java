package sinnet.rpc;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.grpc.Status;
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

@Component
@RequiredArgsConstructor
class ProjectsCreateImpl implements RpcCommandHandler<CreateRequest, CreateReply> {

  private final AccessFacade accessFacade;
  private final DboFacade dboFacade;
  private final ApplicationEventPublisher onProjectCreated;

  @Override
  public CreateReply apply(CreateRequest cmd) {
    var requestor = cmd.getUserToken();

    var valId = ValProjectId.of(UUID.randomUUID());
    var emailOfRequestor = ValEmail.of(requestor.getRequestorEmail());
    accessFacade.guardAccess(requestor, valId, roleContext -> roleContext::canCreateProject);

    var createResult = dboFacade.create(new DboCreate.CreateContent(valId, emailOfRequestor));
    if (createResult instanceof DboCreate.Success x) {
      var evt = ProjectCreatedEvent.newBuilder()
          .setEid(x.getVid().id().toString())
          .setEtag(x.getVid().tag())
          .build();
      onProjectCreated.publishEvent(evt);

      return CreateReply.newBuilder()
          .setEntityId(ProjectId.newBuilder()
              .setEId(valId.value().toString())
              .setETag(1))
          .build();
    } else if (createResult instanceof DboCreate.ValidationFailed x) {
      throw Status.FAILED_PRECONDITION.withDescription(x.getReason()).asRuntimeException();
    } else {
      throw Status.INTERNAL.asRuntimeException();
    }
  }

}
