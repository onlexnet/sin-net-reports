package sinnet.gql.users;

import static sinnet.grpc.PropsBuilder.ofNullable;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.RpcQueryHandler;
import sinnet.grpc.users.SearchReply;
import sinnet.grpc.users.SearchRequest;
import sinnet.grpc.users.UsersSearchModel;
import sinnet.models.Email;
import sinnet.read.UsersRepositoryEx;


@Component
@RequiredArgsConstructor
@Slf4j
public class UsersRpcSearch implements RpcQueryHandler<SearchRequest, SearchReply>, MapperDto {

  private final UsersRepositoryEx usersProvider;

  @Override
  public SearchReply apply(SearchRequest request) {
    var emailAsString = request.getUserToken().getRequestorEmail();
    var projectIdAsString = request.getUserToken().getProjectId();
    var email = Email.of(emailAsString);
    var projectId = UUID.fromString(projectIdAsString);
    var result = usersProvider.search(projectId, email);
    var asDto = toDto(result);
    return SearchReply.newBuilder()
        .addAllItems(asDto)
        .build();
  }

}

// private final ServicemanRepository repository;

// @Override
// public EntityId onRequest(Command msg) {
//   var entityId = EntityId.anyNew(msg.projectId());

//   var state = new ServicemanState(entityId, msg.email(), msg.fullName());

//     var projectEntityId = msg.projectId();
//     var eid = entityId.getId();
//     var etag = entityId.getVersion();

//     var entry = new ServicemanDbo()
//         .setProjectEntityId(projectEntityId.getId())
//         .setEntityId(eid)
//         .setEntityVersion(etag)
//         .setEmail(state.getEmail().getValue())
//         .setCustomName(state.getName());
//     var result = repository.save(entry);
//     return EntityId.of(result.getEntityId(), eid, result.getEntityVersion());
//   }
