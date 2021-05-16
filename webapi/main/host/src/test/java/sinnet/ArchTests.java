package sinnet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;

/** Architectural tests. */
@AnalyzeClasses(packages = "sinnet")
public class ArchTests {

  // @ArchTest
  // static final ArchRule noDependencyOnUpperPackage = DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

  @ArchTest
  static final ArchRule noPublicControllers = classes().that()
      .areAnnotatedWith(RestController.class)
      .should()
      .bePackagePrivate()
      .andShould()
      .resideInAPackage("sinnet.web");

  @Test
  public void modelsShouldBeIndependent() {

  }
}
