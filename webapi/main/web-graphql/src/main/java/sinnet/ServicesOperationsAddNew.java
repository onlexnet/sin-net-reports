package sinnet;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ServicesOperationsAddNew implements GraphQLResolver<ServicesOperations> {

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param filter fixme.
     * @return fixme
     */
    public CompletableFuture<Boolean> addNew(final ServicesOperations ignored,
                                             final ServicesFilter filter) {
        return CompletableFuture.completedFuture(true);
    }

}
