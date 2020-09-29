package sinnet;

import java.util.UUID;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import io.vavr.collection.List;
import lombok.Value;

@Component
public class Users implements GraphQLQueryResolver {

    public Users getUsers() {
        return this;
    }

    public List<User> search() {
        return new User(UUID.randomUUID(), "aaa@bbb");
    }

}

@Value
class User {
    private UUID entityId;
    private String email;
}
