package sinnet.ports.timeentries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannelBuilder;
import sinnet.grpc.timeentries.TimeEntriesGrpc;
import sinnet.grpc.timeentries.TimeEntriesGrpc.TimeEntriesBlockingStub;
import sinnet.ports.timeentries.GrpcProperties.ServiceAddress;

@Configuration
class ActionsGrpcConfig {

  @Bean
  TimeEntriesBlockingStub timeentriesBlockingStub(Closeable.Of<TimeEntriesBlockingStub> closeableTimeentriesBlockingStub) {
    return closeableTimeentriesBlockingStub.item();
  }

  @Bean
  Closeable.Of<TimeEntriesBlockingStub> closeableTimeentriesBlockingStub(GrpcProperties props) {
    return managedChannel(props.getTimeentries());
  }

  Closeable.Of<TimeEntriesBlockingStub> managedChannel(ServiceAddress address) {
    var daprAppId = address.getDaprAppId();
    var channel = ManagedChannelBuilder.forAddress(address.getHost(), address.getPort())
        .intercept(GrpcUtils.addTargetDaprApplicationId(daprAppId))
        .usePlaintext()
        .build();
    var service = TimeEntriesGrpc.newBlockingStub(channel);
    var channelDisposer = GrpcUtils.asCloseable(channel);
    return Closeable.of(service, channelDisposer);
  }
}
