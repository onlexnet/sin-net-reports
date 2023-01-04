package sinnet.dbo;

import java.util.UUID;

import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;

/** Support for update commands. */
public interface DboUpdate {

  /** Same as {@link #updateCommand(ProjectVid, UpdateCommandContent)} but using Dto types. */
  default UpdateResult update(UpdateCommand request) {
    var eidAsString = request.getEntityId().getEId();
    var eidAsUuid = UUID.fromString(eidAsString);
    var etag = request.getEntityId().getETag();
    var vid = ProjectVid.of(eidAsUuid, etag);

    var desired = request.getDesired();
    var name = desired.getName();
    var emailOfOwner = ValEmail.of(desired.getEmailOfOwner());
    var operators = desired.getEmailOfOperatorList().stream().map(ValEmail::of).toArray(ValEmail[]::new);
    var content = new UpdateCommandContent(name, emailOfOwner, operators);

    var it = this.updateCommand(vid, content);
    return UpdateResult.newBuilder()
            .setEntityId(ProjectId.newBuilder()
            .setEId(it.id().toString())
            .setETag(it.tag()))
          .build();
  }

  ProjectVid updateCommand(ProjectVid vid, UpdateCommandContent content);

  /** Container for data used to update Project entity. */
  record UpdateCommandContent(String name, ValEmail newOwner, ValEmail... operators) {}
}
