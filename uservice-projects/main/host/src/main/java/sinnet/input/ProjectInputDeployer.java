package sinnet.input;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.core.runtime.context.VertxContextSafetyToggle;
import io.smallrye.common.vertx.VertxContext;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
public class ProjectInputDeployer {

  private final MyHandler myHandler;

  // Low-level non-reactive vert.x context creation because of https://github.com/quarkusio/quarkus/issues/27854
  public void init(@Observes StartupEvent e, Vertx vertx, ProjectInputService verticle) {
    var ctx = VertxContext.getOrCreateDuplicatedContext(vertx);
    VertxContextSafetyToggle.setContextSafe(ctx, true);
    ctx.runOnContext(myHandler);
  }
}

@RequiredArgsConstructor
@ApplicationScoped
class MyHandler implements Handler<Void> {

  private final ProjectInputService inputService;

  @Override
  public void handle(Void event) {
    inputService.run().subscribe().with(v -> {});
  }

}
