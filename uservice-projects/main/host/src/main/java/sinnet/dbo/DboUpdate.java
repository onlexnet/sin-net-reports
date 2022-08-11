package sinnet.dbo;

import io.smallrye.mutiny.Uni;
import sinnet.grpc.projects.UpdateCommand;
import sinnet.grpc.projects.UpdateResult;

public interface DboUpdate {

  Uni<UpdateResult> update(UpdateCommand request);
}
