package sinnet;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import javax.persistence.Entity;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.library.DependencyRules;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(packages = "sinnet")
public class AppArchTest {
  
  @ArchTest
  static final ArchRule noDependencyOnUpperPackage = DependencyRules.NO_CLASSES_SHOULD_DEPEND_UPPER_PACKAGES;

  // Classes should not access System.in , System.out or System.err
  // Classes should not use java util logging
  // Classes should not use joda time
  // Methods should not throw generic exception
  // For spring classes, there should be any field injection (@Autowired), use constrctor injection
  @ArchTest
  static final ArchRule implement_general_coding_practices = CompositeArchRule.of(GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING)
      .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME)
      // .and(GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION)
      .because("These are Voilation of general coding rules");


  @ArchTest
  static final ArchRule dboNameAndVisibility = classes().that()
      .areAnnotatedWith(Entity.class)
      .should()
      .bePackagePrivate()
      .andShould()
      .haveSimpleNameEndingWith("Dbo");
      
}
