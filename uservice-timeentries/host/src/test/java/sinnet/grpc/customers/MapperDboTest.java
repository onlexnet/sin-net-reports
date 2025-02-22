package sinnet.grpc.customers;

import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

public class MapperDboTest implements MapperDbo {

  @Test
  void shouldMapCustomerFull() {

    var dbo = Instancio.create(CustomerRepository.CustomerDbo.class);

    this.fromDbo(dbo);
  }

  @Test
  void shouldMapCustomerEmpty() {

    var dbo = Instancio.createBlank(CustomerRepository.CustomerDbo.class);

    this.fromDbo(dbo);
  }
}
