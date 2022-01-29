package sinnet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;

@Component
public class GrpcServer {

    @Autowired
    MyService myService;

    @Value("${grpc.server.port}") int port;

    private Server server;

    @PostConstruct
    @SneakyThrows
    public void start() {
        var builder = ServerBuilder.forPort(port);
        server = builder.addService(myService).build();

        server.start();
    }
}
