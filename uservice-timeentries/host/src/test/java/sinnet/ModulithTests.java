package sinnet;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Spring Modulith architecture tests.
 * These tests verify that the module structure is valid and modules
 * don't have cyclic dependencies.
 */
class ModulithTests {

  ApplicationModules modules = ApplicationModules.of(Application.class);

  @Test
  void verifyModuleStructure() {
    modules.verify();
  }

  @Test
  void createModuleDocumentation() {
    new Documenter(modules)
        .writeDocumentation();
  }
}
