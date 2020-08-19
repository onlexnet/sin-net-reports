package sinnet;

import java.util.concurrent.CompletableFuture;

import org.axonframework.commandhandling.callbacks.FutureCallback;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ServicesOperationsAddNew implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private CommandGateway commandGateway;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param entry fixme.
     * @return fixme
     */
    public CompletableFuture<Boolean> addNew(final ServicesOperations ignored,
                                             final ServiceEntry entry) {
        var cmd = new RegisterNewServiceAction();
        cmd.setWhen(entry.getWhenProvided());
        var callback = new FutureCallback<>();
        commandGateway.send(cmd, callback);
        return callback.thenApply(it -> Boolean.TRUE);
    }

}
