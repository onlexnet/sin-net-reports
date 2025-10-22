package sinnet.ports.timeentries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannelBuilder;
import sinnet.ports.timeentries.Closeable.Of;
import sinnet.ports.timeentries.GrpcProperties.ServiceAddress;
import sinnet.report2.grpc.ReportsGrpc;
import sinnet.report2.grpc.ReportsGrpc.ReportsBlockingStub;

@Configuration
class Reports2GrpcConfig {

  @Bean
  ReportsBlockingStub reports2BlockingStub(Closeable.Of<ReportsBlockingStub> closeableReports2BlockingStub) {
    return closeableReports2BlockingStub.item();
  }

  @Bean
  Closeable.Of<ReportsBlockingStub> closeableReports2BlockingStub(GrpcProperties props) {
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
