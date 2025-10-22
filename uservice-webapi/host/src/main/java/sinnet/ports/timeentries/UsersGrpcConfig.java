package sinnet.ports.timeentries;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ManagedChannelBuilder;
import sinnet.grpc.users.UsersGrpc;
import sinnet.grpc.users.UsersGrpc.UsersBlockingStub;
import sinnet.ports.timeentries.GrpcProperties.ServiceAddress;

@Configuration
class UsersGrpcConfig {

  @Bean
  UsersBlockingStub usersBlockingStub(Closeable.Of<UsersBlockingStub> closeableUsersBlockingStub) {
    return closeableUsersBlockingStub.item();
  }

  @Bean
  Closeable.Of<UsersBlockingStub> closeableUsersBlockingStub(GrpcProperties props) {
    return managedChannel(props.getUsers());
  }

  Closeable.Of<UsersBlockingStub> managedChannel(ServiceAddress address) {
    var daprAppId = address.getDaprAppId();
    var channel = ManagedChannelBuilder.forAddress(address.getHost(), address.getPort())
        .intercept(GrpcUtils.addTargetDaprApplicationId(daprAppId))
        .usePlaintext()
        .build();
    var service = UsersGrpc.newBlockingStub(channel);
    var channelDisposer = GrpcUtils.asCloseable(channel);
    return Closeable.of(service, channelDisposer);
  }
}
