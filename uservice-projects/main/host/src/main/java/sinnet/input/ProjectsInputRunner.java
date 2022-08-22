package sinnet.input;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.mutiny.core.Vertx;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.dbo.DboFacade;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
class ProjectsInputRunner {

  private final DboFacade dboFacade;
  private final Vertx vertx;

  void onStart(@Observes StartupEvent ev) {
    vertx.deployVerticle(new ProjectInputService(dboFacade));
  }

  void onStop(@Observes ShutdownEvent ev) {               
  }
}