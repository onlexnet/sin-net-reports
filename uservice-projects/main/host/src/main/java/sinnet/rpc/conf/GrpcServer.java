package sinnet.rpc.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import sinnet.host.AppGrpcProperties;

/** Registers all discoverable gRpc services to allow them be reachable. */
@Component
@RequiredArgsConstructor
class GrpcServer implements AutoCloseable {
  private final AppGrpcProperties grpcProperties;
  private final BindableService[] services;

  private Server server;

  @jakarta.annotation.PostConstruct
  @SneakyThrows
  public void start() {
    var port = grpcProperties.getServerPort();
    var builder = ServerBuilder.forPort(port);
    for (var bindableService : services) {
      builder.addService(bindableService);
    }
    server = builder
      .intercept(new ExceptionHandler())
      .build();
    server.start();
  }

  @Override
  public void close() throws Exception {
    server.shutdownNow();
    server.awaitTermination();
  }
}
