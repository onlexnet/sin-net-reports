package sinnet.rpc;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboFacade;
import sinnet.grpc.projects.UserStatsReply;
import sinnet.grpc.projects.UserStatsRequest;
import sinnet.model.ValEmail;

@Component
@RequiredArgsConstructor
final class ProjectUserStatsImpl implements RpcQueryHandler<UserStatsRequest, UserStatsReply> {

  private final DboFacade dboFacade;

  @Override
  public UserStatsReply apply(UserStatsRequest request) {
    var userEmailAsString = request.getEmailOfRequestor();
    var userEmail = ValEmail.of(userEmailAsString);
    
    var result = dboFacade.getStats(userEmail);
    return UserStatsReply.newBuilder().setNumberOfProjects(result.numberOdProjects()).build();
  }

}
