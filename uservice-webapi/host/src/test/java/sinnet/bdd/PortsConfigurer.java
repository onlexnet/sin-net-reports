package sinnet.bdd;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import sinnet.grpc.ActionsGrpcFacade;
import sinnet.grpc.CustomersGrpcFacade;
import sinnet.grpc.ProjectsGrpcFacade;
import sinnet.grpc.Reports1GrpcAdapter;
import sinnet.grpc.UsersGrpcService;
import sinnet.infra.TimeProvider;
import sinnet.otp.OtpGeneratorTests;

/**
 * Place where we keep all ports which are mocked as part of all DDD scenarios.
 * This class provides mock implementations of all external services for testing.
 */
@Configuration
class PortsConfigurer {

  /**
   * Creates a mock for ProjectsGrpcFacade to be used in tests.
   * @return mocked ProjectsGrpcFacade bean
   */
  @Bean
  @Primary
  ProjectsGrpcFacade projectsGrpcServiceMocked() {
    return Mockito.mock(ProjectsGrpcFacade.class);
  }

  /**
   * Creates a mock for CustomersGrpcFacade to be used in tests.
   * @return mocked CustomersGrpcFacade bean
   */
  @Bean
  @Primary
  CustomersGrpcFacade customersGrpcServiceMocked() {
    return Mockito.mock(CustomersGrpcFacade.class);
  }

  /**
   * Creates a mock for UsersGrpcService to be used in tests.
   * @return mocked UsersGrpcService bean
   */
  @Bean
  @Primary
  UsersGrpcService usersGrpcServiceMocked() {
    return Mockito.mock(UsersGrpcService.class);
  }

  /**
   * Creates a mock for ActionsGrpcFacade that calls real methods.
   * @return mocked ActionsGrpcFacade bean that delegates to real methods
   */
  @Bean
  @Primary
  ActionsGrpcFacade actionsGrpcFacade() {
    return Mockito.mock(ActionsGrpcFacade.class, Answers.CALLS_REAL_METHODS);
  }
  
  /**
   * Creates a mock for Reports1GrpcAdapter that returns default values.
   * @return mocked Reports1GrpcAdapter bean that returns default values for all methods
   */
  @Bean
  @Primary
  Reports1GrpcAdapter reports1GrpcAdapter() {
    return Mockito.mock(Reports1GrpcAdapter.class, Answers.RETURNS_DEFAULTS);
  }

  /**
   * Creates a TimeProvider implementation that always returns the same fixed timestamp.
   * @return TimeProvider that returns a fixed test timestamp
   */
  @Bean
  @Primary
  TimeProvider timeProvider() {
    return () -> OtpGeneratorTests.examplePeriod;
  }

}
