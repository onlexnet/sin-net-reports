package sinnet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.GreeterGrpc.GreeterFutureStub;
import sinnet.reports.ReportsGrpc;
import sinnet.reports.ReportsGrpc.ReportsFutureStub;

@Configuration
class InfraReportsConfigurer {

  @Value("${app.infra.report.address}")
  private String infraReportAddress;

  @Bean
  public ManagedChannel infraReportsChannel() {
    return ManagedChannelBuilder.forAddress(infraReportAddress, 9000)
        .usePlaintext()
        .build();
  }

  @Bean
  public GreeterFutureStub infraGreeterFutureStub(ManagedChannel infraReportsChannel) {
    return GreeterGrpc.newFutureStub(infraReportsChannel);
  }

  @Bean
  public ReportsFutureStub infraReportsFutureStub(ManagedChannel infraReportsChannel) {
    return ReportsGrpc.newFutureStub(infraReportsChannel);
  }
}
