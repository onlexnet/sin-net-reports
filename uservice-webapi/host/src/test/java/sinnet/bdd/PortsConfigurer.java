package sinnet.bdd;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import sinnet.app.ports.out.ActionsGrpcPortOut;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.app.ports.out.ProjectsPortOut;
import sinnet.app.ports.out.UsersServicePortOut;

/**
 * Place where we keep all ports which are mocked as part of all DDD scenarions.
 */
class PortsConfigurer {

  @Bean
  @Primary
  ProjectsPortOut projectsGrpcServiceMocked() {
    var projectsGrpcService = Mockito.mock(ProjectsPortOut.class);
    return projectsGrpcService;
  }

  @Bean
  @Primary
  CustomersPortOut customersGrpcServiceMocked() {
    var service = Mockito.mock(CustomersPortOut.class);
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
