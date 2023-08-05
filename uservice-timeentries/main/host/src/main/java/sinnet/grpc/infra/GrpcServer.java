package sinnet.grpc.infra;

import java.util.OptionalInt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.Getter;
import lombok.SneakyThrows;

/** Registers all discoverable gRpc services to allow them be reachable. */
@Component
public final class GrpcServer implements AutoCloseable {

  private final BindableService[] services;
  private int desiredPort;

  @Getter
  private OptionalInt serverPort = OptionalInt.empty();

  public GrpcServer(BindableService[] services,
                    @Value("${grpc.server.port}") int port) {
    this.services = services;
    desiredPort = port;
  }

  private Server server;

  /**
   * TBD.
   */
  @jakarta.annotation.PostConstruct
  @SneakyThrows
  public void start() {
    var builder = ServerBuilder.forPort(desiredPort);
    for (var bindableService : services) {
      builder.addService(bindableService);
    }
    server = builder
      .intercept(new ExceptionHandler())
      .build();
    server.start();

    serverPort = OptionalInt.of(server.getPort());
  }

  @Override
  public void close() throws Exception {
    server.shutdownNow();
  }
}
