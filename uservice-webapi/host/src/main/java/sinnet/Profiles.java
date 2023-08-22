package sinnet;

import lombok.experimental.UtilityClass;

/** List of all supported profiles with its descriptions. */
public interface Profiles {

  /** Possible values of ap[plication settings. */
  @UtilityClass
  class App {
    public static final String DEV = "dev";
    public static final String PROD = "prod";
    public static final String TEST = "test";
  }
}
