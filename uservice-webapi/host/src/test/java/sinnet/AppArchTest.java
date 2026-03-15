package sinnet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.Architectures.LayeredArchitecture;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(packages = "sinnet", importOptions = ImportOption.DoNotIncludeTests.class)
public class AppArchTest {

  @ArchTest
  static final ArchRule noDependencyOnUpperPackage = noClasses().should(new DependOnUpperPackagesCondition())
      .because("that might prevent packages on that level from being split into separate artifacts in a clean way");;

  static final String ROOT_PACKAGE_PORTS = "sinnet.app.ports..";
  static final String ROOT_PACKAGE_PORTS_OUT = "sinnet.app.ports.out..";
  static final String ROOT_PACKAGE_DOMAIN = "sinnet.domain..";
  static final String ROOT_PACKAGE_GQL = "sinnet.gql..";
  static final String ROOT_PACKAGE_APP = "sinnet.app..";
  static final String ROOT_PACKAGE_INFRA = "sinnet.infra..";

  static LayeredArchitecture layers = Architectures.layeredArchitecture()
          .consideringAllDependencies()
          .layer("Adapters").definedBy("sinnet.infra.adapters..")
          .layer("PortsIn").definedBy("sinnet.app.ports.in..")
          .layer("PortsOut").definedBy(ROOT_PACKAGE_PORTS_OUT)
          .layer("LegacyPorts").definedBy(ROOT_PACKAGE_PORTS)
          .layer("Domain").definedBy(ROOT_PACKAGE_DOMAIN)
          .layer("GQL").definedBy(ROOT_PACKAGE_GQL)
          .layer("App").definedBy(ROOT_PACKAGE_APP)
          .layer("AppFlow").definedBy("sinnet.app.flow..")
          .layer("AppServices").definedBy("sinnet.app.service..")
          .layer("Infra").definedBy(ROOT_PACKAGE_INFRA)
          .layer("grpc.models").definedBy("sinnet.grpc..")
          .layer("grpc.adapters").definedBy("sinnet.infra.adapters.grpc..")
          .layer("gql.models").definedBy("sinnet.gql..")
          .layer("gql.adapters").definedBy("sinnet.infra.adapters.gql..");


  // Classes should not access System.in , System.out or System.err
  // Classes should not use java util logging
  // Classes should not use joda time
  // Methods should not throw generic exception
  // For spring classes, there should be any field injection (@Autowired), use
  // constrctor injection
  @ArchTest
  static final ArchRule implement_general_coding_practices = CompositeArchRule
      .of(GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION)
      .because("These are Voilation of general coding rules");


    @Test
    void shouldSeparateModules() {

      var testedClasses = new ClassFileImporter()
          .withImportOption(new ImportOption.DoNotIncludeTests())
          .importPackages("sinnet");

      // Ports should not depend on domain
      layers
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Adapters", "PortsIn", "PortsOut", "LegacyPorts", "App", "GQL")
        .check(testedClasses);

      layers
        .whereLayer("PortsOut").mayOnlyBeAccessedByLayers("AppFlow", "AppServices", "Adapters")
        .check(testedClasses);
      
      layers
        .whereLayer("grpc.models").mayOnlyBeAccessedByLayers("grpc.adapters")
        .check(testedClasses);

      layers
        .whereLayer("gql.models").mayOnlyBeAccessedByLayers("gql.adapters")
        .check(testedClasses);

      layers
        .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
        .check(testedClasses);
      
      layers
        .whereLayer("Infra").mayNotBeAccessedByAnyLayer()
        .check(testedClasses);

    }
}

// Copy of DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES from ArchUnit
// suite, but ignoring dependency on clases named '...PackageMarker'
class DependOnUpperPackagesCondition extends ArchCondition<JavaClass> {

  DependOnUpperPackagesCondition() {
    super("depend on upper packages");
  }

  @Override
  public void check(final JavaClass clazz, final ConditionEvents events) {
    for (var dependency : clazz.getDirectDependenciesFromSelf()) {
      var dependencyOnUpperPackage = isDependencyOnUpperPackage(dependency.getOriginClass(),
          dependency.getTargetClass());
      events.add(new SimpleConditionEvent(dependency, dependencyOnUpperPackage, dependency.getDescription()));
    }
  }

  private boolean isDependencyOnUpperPackage(JavaClass origin, JavaClass target) {
    if (isPackageMarker(target)) {
      return false;
    }
    if (isPortAdapter(target)) {
      return false;
    }
    String originPackageName = origin.getPackageName();
    String targetSubPackagePrefix = target.getPackageName() + ".";
    return originPackageName.startsWith(targetSubPackagePrefix);
  }

  private boolean isPackageMarker(JavaClass target) {
    return target.getSimpleName().endsWith("PackageMarker");
  }

  private boolean isPortAdapter(JavaClass target) {
    return target.getSimpleName().endsWith("Facade");
  }

}
