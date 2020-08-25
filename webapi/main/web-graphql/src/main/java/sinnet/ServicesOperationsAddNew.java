package sinnet;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/** Fixme. */
@Component
public class ServicesOperationsAddNew implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private Vertx vertx;

    @Autowired
    private CommandEntry commandEntry;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param entry fixme.
     * @return fixme
     */
    public CompletableFuture<Boolean> addNew(final ServicesOperations ignored,
                                             final ServiceEntry entry) {

        var resultMidStep = new CompletableFuture<Boolean>();
        var listener = vertx.eventBus().<JsonObject>localConsumer("aaa",  evt -> {
            resultMidStep.complete(Boolean.TRUE);
        });
        final int maxTimeout = 30;
        var result = resultMidStep
            .completeOnTimeout(Boolean.FALSE, maxTimeout, TimeUnit.SECONDS)
            .exceptionally(ex -> Boolean.FALSE)
            .thenApply(it -> {
                listener.unregister();
                return it;
            });

        var cmd = new RegisterNewServiceAction();
        cmd.setWhen(entry.getWhenProvided());
        commandEntry
            .send(cmd)
            .thenApply(it -> result.complete(Boolean.TRUE));
        return result;
    }

}
