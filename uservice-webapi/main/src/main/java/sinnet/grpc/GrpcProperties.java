package sinnet.grpc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@ConfigurationProperties("app.grpc")
@Data
@Validated
class GrpcProperties {
  @NotNull
  private ServiceAddress projects;
  @NotNull
  private ServiceAddress rbac;

  @Data
  @Validated
  static class ServiceAddress {
    @NotNull
    private String daprAppId;
    @NotNull
    private String host;
    @NotNull
    private Integer port;
  }

}