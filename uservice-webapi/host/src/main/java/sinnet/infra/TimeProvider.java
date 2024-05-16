package sinnet.infra;

import java.time.LocalDateTime;

/** Testable replacement of LocaDateTime.now() */
public interface TimeProvider {
  
  LocalDateTime now();

}
