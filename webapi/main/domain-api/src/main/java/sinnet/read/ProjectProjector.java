package sinnet.read;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import lombok.Value;
import sinnet.models.Email;
import sinnet.models.ProjectId;

/** Container interface to keep together Provider and its models. */
public interface ProjectProjector {

  /** Provides projections where Project is the main subject of the query. */
  interface Provider {

    Future<Array<FindByServicemanModel>> findByServiceman(Email email);
  }

  /** Local model to keep data of a projection. */
  @Value
  class FindByServicemanModel {
    private ProjectId id;
    private String name;
  }

}
