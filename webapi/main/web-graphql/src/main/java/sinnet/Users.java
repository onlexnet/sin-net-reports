package sinnet;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import io.vavr.collection.Stream;
import lombok.Value;
import sinnet.models.Email;

@Component
public class Users implements GraphQLQueryResolver {

    @Autowired
    private UsersProvider usersProvider;

    @Autowired
    private IdentityProvider identity;

    public Users getUsers() {
        return this;
    }

    public Iterable<UserDTO> search() {
        return identity
            .getCurrent()
            .map(user -> {
                var email = Email.of(user.getEmail());
                return usersProvider
                    .search(email)
                    .block()
                    .map(it -> new UserDTO(UUID.randomUUID(), it.getEmail().getValue()));
            })
            .orElseGet(Stream::empty);
    }

}

@Value
class UserDTO {
    private UUID entityId;
    private String email;
}
