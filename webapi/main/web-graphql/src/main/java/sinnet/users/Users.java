package sinnet.users;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.collection.Stream;
import lombok.Value;
import sinnet.IdentityProvider;
import sinnet.UsersProvider;
import sinnet.models.Email;

@Value
public class Users {
    private UUID projectId;
}

@Component
class UsersQuery implements GraphQLQueryResolver {

    public Users getUsers(UUID projectId) {
        return new Users(projectId);
    }

}

@Component
class UsersQuerySearch implements GraphQLResolver<Users> {

    @Autowired
    private UsersProvider usersProvider;

    @Autowired
    private IdentityProvider identity;

    public Iterable<UserDTO> search(Users gqlContext) {
        return identity
            .getCurrent()
            .map(user -> {
                var email = Email.of(user.getEmail());
                return usersProvider
                    .search(gqlContext.getProjectId(), email)
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
