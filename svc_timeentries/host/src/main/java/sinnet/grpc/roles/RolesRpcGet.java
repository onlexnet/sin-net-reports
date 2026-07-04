package sinnet.grpc.roles;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.AllArgsConstructor;
import sinnet.domain.model.ValEmail;
import sinnet.read.RolesProjector;

/**
 * TBD.
 */
@Component
@AllArgsConstructor
public class RolesRpcGet implements Mapper {
  private final RolesProjector.Provider rolesProjector;

  /**
   * TBD.
   */
  public void query(GetRequest request, StreamObserver<GetReply> responseObserver) {
    var userToken = request.getUserToken();
    var emailAsString = userToken.getRequestorEmail();
    var email = ValEmail.of(emailAsString);
    var projectIdAsString = userToken.getProjectId();
    var projectId = UUID.fromString(projectIdAsString);
    var result = rolesProjector.find(email, projectId);
    var reply = GetReply.newBuilder()
        .setRole(toDto(result))
        .build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}
