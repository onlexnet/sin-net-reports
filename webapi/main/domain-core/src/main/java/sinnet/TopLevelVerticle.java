package sinnet;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import io.vavr.collection.Stream;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public interface TopLevelVerticle extends Verticle {
}

@Component
class VerticleRegistrar {

    @Autowired
    private TopLevelVerticle[] verticlesToRegister;
    private Iterable<String> verticlesToUndeploy;

    @Autowired
    private Vertx vertx;

    @PostConstruct
    void init() {
        var syncGuard = new CompletableFuture<>();

        var startedDeployments = Array.of(verticlesToRegister)
            .map(it -> vertx.deployVerticle(it))
            .collect(Collectors.toList());

        CompositeFuture.all((List) startedDeployments)
            .onSuccess(it -> syncGuard.complete(null))
            .onFailure(ex -> syncGuard.completeExceptionally(ex));

        syncGuard.join();

        verticlesToUndeploy = startedDeployments.stream()
            .map(it -> it.result())
            .collect(Collectors.toList());
    }

    @PreDestroy
    void dispose() {
        var syncGuard = new CompletableFuture<>();

        var undeployments = Stream.ofAll(verticlesToUndeploy)
            .map(it -> vertx.undeploy(it));
        CompositeFuture
            .join((List) undeployments)
            .onComplete(it -> syncGuard.complete(null));

        syncGuard.join();
    }
}
