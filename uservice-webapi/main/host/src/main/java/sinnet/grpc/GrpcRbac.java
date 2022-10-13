package sinnet.grpc;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import io.grpc.Metadata;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.roles.Rbac;

@ApplicationScoped
@RequiredArgsConstructor
public class GrpcRbac{

 
  private final @GrpcClient("uservice-activities-config") Rbac service;

  @Delegate
  private Rbac interceptedService;

  @PostConstruct
  void init() {
    var extraHeaders = new Metadata();
    var key = Metadata.Key.of("dapr-app-id", Metadata.ASCII_STRING_MARSHALLER);
    extraHeaders.put(key, "activities-app-id");
    
    interceptedService = GrpcClientUtils.attachHeaders(service, extraHeaders);
  }
}