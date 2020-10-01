package sinnet;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLMutationResolver;

@Component
public class DevMutation implements GraphQLMutationResolver {

    public DevMutation getDev() {
        return this;
    }

    public boolean setUser(String email) {
        return true;
    }
}
