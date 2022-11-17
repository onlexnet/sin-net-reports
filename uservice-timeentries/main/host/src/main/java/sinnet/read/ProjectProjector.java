package sinnet.read;

import io.vavr.collection.Array;
import lombok.Value;
import sinnet.models.Email;
import sinnet.models.ProjectId;

/** Container interface to keep together Provider and its models. */
public interface ProjectProjector {

  /** Provides projections where Project is the main subject of the query. */
  interface Provider {

    /**
     * Returns list of projects where is already assigned serviceman with provided email.
     */
    Array<FindByServicemanModel> findByServiceman(Email email);
  }

  /** Local model to keep data of a projection. */
  @Value
  class FindByServicemanModel {
    private ProjectId id;
    private String name;
  }

}
