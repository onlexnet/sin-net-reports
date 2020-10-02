package sinnet;

import io.vavr.collection.Stream;

public interface UsersProvider {
    Stream<UserModel> search();
}
