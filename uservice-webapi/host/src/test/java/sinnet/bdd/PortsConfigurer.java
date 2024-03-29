package sinnet.bdd;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.ProjectsGrpcFacade;
import sinnet.grpc.UsersGrpcService;

/**
 * Place where we keep all ports which are mocked as part of all DDD scenarions.
 * AS I am in r
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
  UsersGrpcService usersGrpcServiceMocked() {
    var service = Mockito.mock(UsersGrpcService.class);
    return service;
  }

  @Bean
  @Primary
  ActionsGrpcFacade actionsGrpcFacade() {
    return Mockito.mock(ActionsGrpcFacade.class, Answers.CALLS_REAL_METHODS);
  }

}
