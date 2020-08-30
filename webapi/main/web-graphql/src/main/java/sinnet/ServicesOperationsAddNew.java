package sinnet;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Mono;
import sinnet.appevents.ServicesProjection;

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

        var correlactionId = UUID.randomUUID();

        var resultMidStep = new CompletableFuture<Boolean>();
        var listener = vertx.eventBus().<JsonObject>localConsumer("aaa",  jevt -> {
            var evt = jevt.body().mapTo(ServicesProjection.Changed.class);
            if (!Objects.equals(evt.getCorrelactionId(), correlactionId)) return;
            resultMidStep.complete(Boolean.TRUE);
        });
        final int maxTimeout = 30;
        var servicesChanged = resultMidStep
            .completeOnTimeout(Boolean.FALSE, maxTimeout, TimeUnit.SECONDS)
            .exceptionally(ex -> Boolean.FALSE)
            .thenApply(it -> {
                listener.unregister();
                return it;
            });


        var cmd = new RegisterNewServiceAction();
        cmd.setWhen(entry.getWhenProvided());
        cmd.setWhat(entry.getDescription());
        var commandResult = commandEntry
            .send(cmd, correlactionId)
            .thenApplyAsync(it -> Boolean.TRUE)
            .exceptionally(it -> Boolean.FALSE);


        return Mono
            .zip(
                Mono.fromCompletionStage(servicesChanged),
                Mono.fromCompletionStage(commandResult))
            .map(it -> it.getT1() & it.getT2())
            .toFuture();

    }

}
