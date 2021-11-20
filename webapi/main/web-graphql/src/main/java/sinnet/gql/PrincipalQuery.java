package sinnet.gql;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.Value;

/** Fixme. */   
@Component
public class PrincipalQuery implements GraphQLQueryResolver {


    /**
     * Fixme.
     * @return fixme
     */
    public PrincipalModel getPrincipal() {
        var c = SecurityContextHolder.getContext();
        var a = c.getAuthentication();
        var p = a.getPrincipal();
        return new PrincipalModel(p.toString());
    }
}

/** Fixme. */
@Value
class PrincipalModel {
    /** Fixme. */
    private String name;
}
