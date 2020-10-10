package sinnet;

import io.vavr.collection.Stream;
import reactor.core.publisher.Mono;
import sinnet.models.Email;

public interface UsersProvider {
    Mono<Stream<UserModel>> search(Email serviceMan);
}
