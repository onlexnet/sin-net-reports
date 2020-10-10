package sinnet;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;
import sinnet.models.Email;

@Component
public class Users implements GraphQLQueryResolver {

    @Autowired
    private UsersProvider usersProvider;

    @Autowired
    private Identity identity;

    public Users getUsers() {
        return this;
    }

    public Iterable<User> search() {
        return usersProvider
            .search(Email.of(identity.getEmail()))
            .block()
            .map(it -> new User(UUID.randomUUID(), it.getEmail().getValue()));
    }

}

@Value
class User {
    private UUID entityId;
    private String email;
}
