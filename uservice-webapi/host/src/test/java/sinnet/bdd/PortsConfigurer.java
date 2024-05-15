package sinnet.bdd;

import java.time.LocalDateTime;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.ProjectsGrpcFacade;
import sinnet.grpc.UsersGrpcService;
import sinnet.infra.TimeProvider;

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
  UsersGrpcService usersGrpcServiceMocked() {
    var service = Mockito.mock(UsersGrpcService.class);
    return service;
  }

  @Bean
  @Primary
  ActionsGrpcFacade actionsGrpcFacade() {
    return Mockito.mock(ActionsGrpcFacade.class, Answers.CALLS_REAL_METHODS);
  }

  @Bean
  @Primary
  TimeProvider timeProvider() {
    return new TimeProvider() {
      @Override
      public LocalDateTime now() {
        return LocalDateTime.of(2001, 2, 3, 4, 5, 6);
      }
    };
  }

}
