package sinnet;

import java.io.File;
import java.util.Map;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.DockerComposeContainer.RemoveImages;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class InitDbTestResource implements QuarkusTestResourceLifecycleManager {

  private static final String SERVICE_INITDB = "init-db";

  private DockerComposeContainer<?> container;
  private Runnable stop = () -> { };

  @Override
  public Map<String, String> start() {
    container = new DockerComposeContainer<>(new File("./compose-test-services.yaml"))
        // only local compose has access to local folders. Change to false to see strange error in test
        .withLocalCompose(true)
        .withBuild(true)
        .withRemoveImages(RemoveImages.LOCAL)
        .withLogConsumer(SERVICE_INITDB, new Slf4jLogConsumer(log))
        .waitingFor(SERVICE_INITDB, Wait.forLogMessage(".*Successfully released change log lock.*", 1));
    container.start();
    stop = container::stop;
    return Map.of(
      "DATABASE_NAME", "devlocaldb",
      "DATABASE_SCHEMA", "uservice_projects");
  }

  @Override
  public void stop() {
    stop.run();
  }

}
