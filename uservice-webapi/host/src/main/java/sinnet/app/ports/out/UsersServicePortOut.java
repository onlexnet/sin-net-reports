package sinnet.app.ports.out;

import java.util.UUID;

import sinnet.app.flow.request.UsersSearchResult;
import sinnet.domain.models.Email;

/** DoxMe. */
public interface UsersServicePortOut {
    
  /** DoxMe. */
  UsersSearchResult search(UUID projectId, Email requestor);

}
