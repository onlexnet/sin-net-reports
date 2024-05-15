package sinnet.infra;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

@Component
class TimeProviderImpl implements TimeProvider {

  @Override
  public LocalDateTime now() {
    return LocalDateTime.now();
  }
  
}
