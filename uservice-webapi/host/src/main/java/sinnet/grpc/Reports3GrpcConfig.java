package sinnet.grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannelBuilder;
import sinnet.grpc.GrpcProperties.ServiceAddress;
import sinnet.report3.grpc.ReportsGrpc;
import sinnet.report3.grpc.ReportsGrpc.ReportsBlockingStub;

@Configuration
class Reports3GrpcConfig {

  @Bean
  ReportsBlockingStub reports3BlockingStub(Closeable.Of<ReportsBlockingStub> closeableUsersBlockingStub) {
    return closeableUsersBlockingStub.item();
  }

  @Bean
  Closeable.Of<ReportsBlockingStub> closeableUsersBlockingStub(GrpcProperties props) {
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
