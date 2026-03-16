package sinnet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;

class AppArchTest {

  @Test
  void shouldLoadApplicationModules() {
    assertThat(modules().stream()).isNotEmpty();
  }

  @Test
  void shouldPreserveDomainIncomingDependencies() {
    assertOnlyAccessedBy(
        modules(),
        "sinnet.domain",
        Set.of("sinnet.infra.adapters", "sinnet.app.ports.in", "sinnet.app.ports.out", "sinnet.app", "sinnet.gql"));
  }

  @Test
  void shouldPreservePortsOutIncomingDependencies() {
    assertOnlyAccessedBy(
        modules(),
        "sinnet.app.ports.out",
      Set.of("sinnet.app.flow", "sinnet.app.service", "sinnet.infra.adapters", "sinnet.infra"));
  }

  @Test
  void shouldPreventDependenciesToGqlAdaptersModule() {
    assertOnlyAccessedBy(modules(), "sinnet.infra.adapters.gql", Set.of());
  }

  @Test
  void shouldPreventDependenciesToGrpcAdaptersModule() {
    assertOnlyAccessedBy(modules(), "sinnet.infra.adapters.grpc", Set.of());
  }

  @Test
  void shouldPreventDependenciesToWsAdaptersModule() {
    assertOnlyAccessedBy(modules(), "sinnet.infra.adapters.ws", Set.of());
  }

  private static ApplicationModules modules() {
    return ApplicationModules.of(RootPackageMarker.class);
  }

  private static void assertOnlyAccessedBy(
      ApplicationModules modules,
      String targetPackage,
      Set<String> allowedIncomingPackages) {

    var targetModule = moduleForPackage(modules, targetPackage);

    var incomingModules = modules.stream()
        .filter(module -> !module.equals(targetModule))
        .filter(module -> module.getDirectDependencies(modules).contains(targetModule))
        .map(module -> module.getBasePackage().getName())
        .collect(Collectors.toSet());

    if (allowedIncomingPackages.isEmpty()) {
      assertThat(incomingModules).isEmpty();
      return;
    }

    assertThat(incomingModules)
        .allMatch(sourcePackage -> allowedIncomingPackages.stream().anyMatch(sourcePackage::startsWith));
  }

  private static ApplicationModule moduleForPackage(ApplicationModules modules, String packageName) {
    return modules.getModuleForPackage(packageName)
        .orElseThrow(() -> new IllegalStateException("Missing application module for package: " + packageName));
  }
}
