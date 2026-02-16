package sinnet.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class GrpcPropertiesTest {

  @Test
  void testGrpcPropertiesSettersAndGetters() {
    // Arrange
    var properties = new GrpcProperties();
    var serviceAddress = new GrpcProperties.ServiceAddress();
    serviceAddress.setDaprAppId("test-app");
    serviceAddress.setHost("localhost");
    serviceAddress.setPort(8080);

    // Act
    properties.setProjects(serviceAddress);
    properties.setRbac(serviceAddress);
    properties.setCustomers(serviceAddress);
    properties.setUsers(serviceAddress);
    properties.setReports(serviceAddress);
    properties.setTimeentries(serviceAddress);

    // Assert
    assertEquals(serviceAddress, properties.getProjects());
    assertEquals(serviceAddress, properties.getRbac());
    assertEquals(serviceAddress, properties.getCustomers());
    assertEquals(serviceAddress, properties.getUsers());
    assertEquals(serviceAddress, properties.getReports());
    assertEquals(serviceAddress, properties.getTimeentries());
  }

  @Test
  void testServiceAddressSettersAndGetters() {
    // Arrange
    var serviceAddress = new GrpcProperties.ServiceAddress();

    // Act
    serviceAddress.setDaprAppId("test-app");
    serviceAddress.setHost("localhost");
    serviceAddress.setPort(8080);

    // Assert
    assertEquals("test-app", serviceAddress.getDaprAppId());
    assertEquals("localhost", serviceAddress.getHost());
    assertEquals(8080, serviceAddress.getPort());
  }

  @Test
  void testServiceAddressCreation() {
    // Act
    var serviceAddress = new GrpcProperties.ServiceAddress();

    // Assert
    assertNotNull(serviceAddress);
  }

  @Test
  void testGrpcPropertiesCreation() {
    // Act
    var properties = new GrpcProperties();

    // Assert
    assertNotNull(properties);
  }
}