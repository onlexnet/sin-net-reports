package sinnet.grpc;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import io.grpc.Metadata;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcClientUtils;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import sinnet.grpc.projects.Projects;

@ApplicationScoped
@RequiredArgsConstructor
public class GrpcProjects{

  private final @GrpcClient("projects") Projects service;

  @Delegate
  private Projects interceptedService;

  @PostConstruct
  void init() {
    var extraHeaders = new Metadata();
    var key = Metadata.Key.of("dapr-app-id", Metadata.ASCII_STRING_MARSHALLER);
    extraHeaders.put(key, "projects");
    
    interceptedService = GrpcClientUtils.attachHeaders(service, extraHeaders);
  }
}
