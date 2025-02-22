package sinnet.grpc.customers;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

public class CustomersRpcUpdateImplTest {

    @Test
    void testNewIncomingItems() {
      var secret1 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret2 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret3 = Instancio.create(sinnet.models.CustomerSecret.class);

      var incoming = List.of(secret1, secret2, secret3);
      var persisted = List.of(secret1, secret2, secret3);
      var toChange = CustomersRpcUpdateImpl.newIncomingSecrets(incoming, persisted);
      Assertions.assertThat(toChange).isEmpty();
    }

    @Test
    void shouldDiscoverNewIncomingItems() {
      var secret1 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret2 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret3 = Instancio.create(sinnet.models.CustomerSecret.class);

      var secret2Updated = Instancio.create(sinnet.models.CustomerSecret.class);
      var incoming = List.of(secret1, secret2Updated, secret3);
      var persisted = List.of(secret1, secret2, secret3);
      var toChange = CustomersRpcUpdateImpl.newIncomingSecrets(incoming, persisted);
      Assertions.assertThat(toChange).containsExactly(secret2Updated);
    }
  }
