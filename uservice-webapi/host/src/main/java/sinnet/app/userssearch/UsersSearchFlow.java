package sinnet.app.userssearch;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.UsersSearchPortIn;
import sinnet.grpc.common.UserToken;
import sinnet.grpc.users.SearchRequest;
import sinnet.infra.adapters.grpc.UsersGrpcService;

@Component
@RequiredArgsConstructor
class UsersSearchFlow implements UsersSearchPortIn {

  private final UsersGrpcService service;

  @Override
  public Iterable<User> search(UUID projectId, String primaryEmail) {

    var request = SearchRequest.newBuilder()
        .setUserToken(UserToken.newBuilder()
        .setRequestorEmail(primaryEmail)
        .setProjectId(projectId.toString()))
        .build();

    var result = service.search(request);

    return result.getItemsList().stream()
        .map(it -> new User(it.getEmail(), it.getEntityId()))
        .toList();

  }

}
