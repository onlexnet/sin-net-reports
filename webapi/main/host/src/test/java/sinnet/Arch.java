package sinnet;

import com.tngtech.archunit.junit.AnalyzeClasses;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.annotation.Secured;

@AnalyzeClasses(packages = "sinnet.models")
public class Arch {

  @Test
  public void modelsShouldBeIndependent() {

  }
}
