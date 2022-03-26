package sinnet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;


/** Registers all discoverable gRpc services to allow them be reachable. */
@Component
public class GrpcServer {

  private final BindableService[] services;
  private final int port;

  public GrpcServer(BindableService[] services,
                    @Value("${grpc.server.port}") int port) {
    this.services = services;
    this.port = port;
  }

  private Server server;

  @PostConstruct
  @SneakyThrows
  public void start() {
    var builder = ServerBuilder.forPort(port);
    for (var bindableService : services) {
      builder.addService(bindableService);
    }
    server = builder.build();

    server.start();
  }
}
