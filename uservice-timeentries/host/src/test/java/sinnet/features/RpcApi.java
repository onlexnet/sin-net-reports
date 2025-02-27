package sinnet.features;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.AppCallbackGrpc.AppCallbackBlockingStub;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import sinnet.grpc.customers.CustomersGrpc;
import sinnet.grpc.customers.CustomersGrpc.CustomersBlockingStub;
import sinnet.grpc.projects.generated.ProjectsGrpc;
import sinnet.grpc.projects.generated.ProjectsGrpc.ProjectsBlockingStub;
import sinnet.grpc.timeentries.TimeEntriesGrpc;
import sinnet.grpc.timeentries.TimeEntriesGrpc.TimeEntriesBlockingStub;
import sinnet.grpc.users.UsersGrpc;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;
import sinnet.report1.grpc.Reports1;
import sinnet.report1.grpc.ReportsGrpc.ReportsBlockingStub;

@Component
@RequiredArgsConstructor
public class RpcApi implements ApplicationListener<ApplicationReadyEvent> {
  
  private final TestRestTemplate restTemplate;

  @Getter
  private UsersBlockingStub users;

  @Getter
  private TimeEntriesBlockingStub timeentries;

  @Getter
  private CustomersBlockingStub customers;

  @Getter
  private ProjectsBlockingStub projects;

  @Getter
  private ReportsBlockingStub reports1;

  @Getter
  private AppCallbackBlockingStub apiCallback;

  @Value("${grpc.server-port}")
  private int grpcPort;

  @Data
  static public class GrpcActuatorModel {
     private Integer port;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var grpcModel = restTemplate.getForObject("/actuator/grpc", GrpcActuatorModel.class);
    var grpcPort = grpcModel.getPort();

    var channel = ManagedChannelBuilder.forAddress("localhost", grpcPort)
        .usePlaintext()
        .build();

    users = UsersGrpc.newBlockingStub(channel);
    timeentries = TimeEntriesGrpc.newBlockingStub(channel);
    apiCallback = AppCallbackGrpc.newBlockingStub(channel);
    customers = CustomersGrpc.newBlockingStub(channel);
    projects = ProjectsGrpc.newBlockingStub(channel);
    reports1 = sinnet.report1.grpc.ReportsGrpc.newBlockingStub(channel);
  }

}
