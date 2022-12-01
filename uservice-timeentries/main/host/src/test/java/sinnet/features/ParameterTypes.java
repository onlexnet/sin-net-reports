package sinnet.features;

import io.cucumber.java.ParameterType;
import sinnet.models.ValName;

public class ParameterTypes {

  @ParameterType(".+")
  public ValName projectName(String value) {
    return ValName.of(value);
  }

  @ParameterType(".+")
  public ValName operatorName(String value) {
    return ValName.of(value);
  }
}
