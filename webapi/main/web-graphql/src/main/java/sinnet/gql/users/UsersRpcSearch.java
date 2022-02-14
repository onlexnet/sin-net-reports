package sinnet.gql.users;

import static sinnet.grpc.PropsBuilder.ofNullable;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersSearchModel;
import sinnet.models.Email;
import sinnet.read.UsersProjector;
import sinnet.vertx.Handlers;


@Component
@RequiredArgsConstructor
@Slf4j
public class UsersRpcSearch {

  private final UsersProjector usersProvider;

  public void search(SearchRequest request, StreamObserver<SearchReply> responseObserver) {
    var emailAsString = request.getUserToken().getRequestorEmail();
    var projectIdAsString = request.getUserToken().getProjectId();
    var email = Email.of(emailAsString);
    var projectId = UUID.fromString(projectIdAsString);
    usersProvider
        .search(projectId, email)
        .onComplete(Handlers.logged(log, responseObserver, it -> it.foldLeft(
            SearchReply.newBuilder(),
            (acc, o) -> acc.addProjects(PropsBuilder.build(UsersSearchModel.newBuilder())
                .tset(ofNullable(o.getEmail().getValue()), b -> b::setEmail)
                .set(UUID.randomUUID().toString(), b -> b::setEmail)
                .done().build()))
            .build()));
  }
}
