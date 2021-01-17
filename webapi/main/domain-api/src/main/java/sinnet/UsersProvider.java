package sinnet;

import java.util.UUID;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;
import sinnet.models.Email;

public interface UsersProvider {
    Mono<Stream<UserModel>> search(UUID projectId, Email serviceMan);
}
