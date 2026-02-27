package sinnet.app.ports.out;

import java.util.UUID;

import sinnet.domain.models.Email;

/** DoxMe. */
public interface UsersServicePortOut {
    
  /** DoxMe. */
  sinnet.grpc.users.SearchReply search(UUID projectId, Email requestor);

}
