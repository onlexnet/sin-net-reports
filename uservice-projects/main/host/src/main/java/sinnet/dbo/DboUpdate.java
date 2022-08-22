package sinnet.dbo;

import java.util.UUID;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.ProjectId;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;
import sinnet.model.ProjectVid;
import sinnet.model.ValEmail;

public interface DboUpdate {

  /** Same as {@link #updateCommand(ProjectVid, UpdateCommandContent)} but using Dto types. */
  default Uni<UpdateResult> update(UpdateCommand request) {
    var eidAsString = request.getEntityId().getEId();
    var eidAsUuid = UUID.fromString(eidAsString);
    var etag = request.getEntityId().getETag();
    var vid = ProjectVid.of(eidAsUuid, etag);
    var content = new UpdateCommandContent(
        request.getModel().getName(),
        ValEmail.of(request.getModel().getEmailOfOwner()));

    return this.updateCommand(vid, content)
        .map(it -> UpdateResult.newBuilder()
          .setEntityId(ProjectId.newBuilder()
          .setEId(it.id().toString())
          .setETag(it.tag()))
          .build());
  }

  Uni<ProjectVid> updateCommand(ProjectVid vid, UpdateCommandContent content);

  /** Container for data used to update Project entity. */
  record UpdateCommandContent(String name, ValEmail newOwner) {}
}
