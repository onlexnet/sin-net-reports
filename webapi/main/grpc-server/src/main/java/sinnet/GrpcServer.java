package sinnet;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;

@Component
public class GrpcServer {

    @Autowired
    List<BindableService> services;

    @Value("${grpc.server.port}")
    int port;

    private Server server;

  @PostConstruct
  @SneakyThrows
  public void start() {
    var builder = ServerBuilder.forPort(port);
    for (BindableService bindableService : services) {
      builder.addService(bindableService);
    }
    server = builder.build();

    server.start();
  }
}
