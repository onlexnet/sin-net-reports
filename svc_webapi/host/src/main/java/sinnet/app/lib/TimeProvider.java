package sinnet.app.lib;

import java.time.LocalDateTime;

/** Testable replacement of LocaDateTime.now(). */
public interface TimeProvider {
  
  LocalDateTime now();

}
