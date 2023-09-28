package sinnet.grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannelBuilder;
import sinnet.grpc.GrpcProperties.ServiceAddress;
import sinnet.report1.grpc.ReportsGrpc;
import sinnet.report1.grpc.ReportsGrpc.ReportsBlockingStub;

@Configuration
class Reports1GrpcConfig {

  @Bean
  ReportsBlockingStub reports1BlockingStub(Closeable.Of<ReportsBlockingStub> closeableReports1BlockingStub) {
    return closeableReports1BlockingStub.item();
  }

  @Bean
  Closeable.Of<ReportsBlockingStub> closeableReports1BlockingStub(GrpcProperties props) {
    return managedChannel(props.getReports());
  }

  Closeable.Of<ReportsBlockingStub> managedChannel(ServiceAddress address) {
    var daprAppId = address.getDaprAppId();
    var channel = ManagedChannelBuilder.forAddress(address.getHost(), address.getPort())
        .intercept(GrpcUtils.addTargetDaprApplicationId(daprAppId))
        .usePlaintext()
        .build();
    var service = ReportsGrpc.newBlockingStub(channel);
    var channelDisposer = GrpcUtils.asCloseable(channel);
    return Closeable.of(service, channelDisposer);
  }
}
