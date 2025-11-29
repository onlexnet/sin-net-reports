package sinnet.features;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.dapr.v1.AppCallbackGrpc;
import io.dapr.v1.AppCallbackGrpc.AppCallbackBlockingStub;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import sinnet.grpc.customers.CustomersGrpc;
import sinnet.grpc.customers.CustomersGrpc.CustomersBlockingStub;
import sinnet.grpc.projects.generated.ProjectsGrpc;
import sinnet.grpc.projects.generated.ProjectsGrpc.ProjectsBlockingStub;
import sinnet.grpc.timeentries.TimeEntriesGrpc;
import sinnet.grpc.timeentries.TimeEntriesGrpc.TimeEntriesBlockingStub;
import sinnet.grpc.users.UsersGrpc;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;
import sinnet.models.ValName;
import sinnet.report1.grpc.ReportsGrpc.ReportsBlockingStub;

@Component
public class RpcApi implements ApplicationListener<ApplicationReadyEvent> {

  private UseAlias currentCustomer = Current.INSTANCE;

  public ValName getCurrentCustomer(UseAlias alias) {
    var selected = switch(alias) {
      case Alias it -> it;
      case Current ignored -> switch (currentCustomer) {
        case Current ignored2 -> { throw new IllegalStateException("Can't reuse empty Customer"); }
        case Alias a -> a;
      };
    };
    currentCustomer = selected;
    return ValName.of(selected.name());
  }


  sealed interface UseAlias {

    static UseAlias current() {
      return Current.INSTANCE;
    }

    static UseAlias of(ValName alias) {
      return new Alias(alias.getValue());
    }
  }

  enum Current implements UseAlias {
    INSTANCE
  }

  record Alias(String name) implements UseAlias {
  }


  private UseAlias currentOperator = Current.INSTANCE;
  ValName getCurrentOperator(UseAlias proposed) {
    var result = switch(proposed) {
      case Current ignored -> { 
        yield switch(currentOperator) {
          case Current ignored2 -> { throw new IllegalStateException("Can't reuse empty operator"); }
          case Alias a -> a;
        };
      }
      case Alias it -> {
        yield it;
      }
    };
    currentOperator = result;
    return ValName.of(result.name());
  }

  @LocalServerPort
  private int serverPort;

  @Getter
  private UsersBlockingStub users;

  private TimeEntriesBlockingStub timeentries;
  public TimeEntriesBlockingStub getTimeentries(UseAlias requestor) {
    getCurrentOperator(requestor);
    return timeentries;
  }

  private CustomersBlockingStub customers;
  public CustomersBlockingStub getCustomers(UseAlias requestor) {
    getCurrentOperator(requestor);
    return customers;
  }

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
    var restClient = RestClient.builder()
        .baseUrl("http://localhost:" + serverPort)
        .build();
    var grpcModel = restClient.get()
        .uri("/actuator/grpc")
        .retrieve()
        .body(GrpcActuatorModel.class);
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
