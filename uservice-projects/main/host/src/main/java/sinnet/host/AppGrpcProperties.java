package sinnet.host;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@ConfigurationProperties(prefix = "grpc")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppGrpcProperties {
  int serverPort;
}
