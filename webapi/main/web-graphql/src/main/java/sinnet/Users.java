package sinnet;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;

@Component
public class Users implements GraphQLQueryResolver {

    @Autowired
    private UsersProvider usersProvider;

    public Users getUsers() {
        return this;
    }

    public Iterable<User> search() {
        return usersProvider
            .search()
            .map(it -> new User(UUID.randomUUID(), it.getEmail()));
    }

}

@Value
class User {
    private UUID entityId;
    private String email;
}
