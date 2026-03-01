package sinnet.bdd;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.infra.adapters.grpc.CustomersGrpcFacade;
import sinnet.infra.adapters.grpc.ProjectsGrpcFacade;

/**
 * Place where we keep all ports which are mocked as part of all DDD scenarions.
 */
class PortsConfigurer {

  @Bean
  @Primary
  ProjectsGrpcFacade projectsGrpcServiceMocked() {
    var projectsGrpcService = Mockito.mock(ProjectsGrpcFacade.class);
    return projectsGrpcService;
  }

  @Bean
  @Primary
  CustomersGrpcFacade customersGrpcServiceMocked() {
    var service = Mockito.mock(CustomersGrpcFacade.class);
    return service;
  }

  @Bean
  @Primary
  UsersServicePortOut usersGrpcServiceMocked() {
    var service = Mockito.mock(UsersServicePortOut.class);
    return service;
  }

  @Bean
  @Primary
  ActionsGrpcPortOut actionsGrpcFacade() {
    return Mockito.mock(ActionsGrpcPortOut.class, Answers.CALLS_REAL_METHODS);
  }

}
