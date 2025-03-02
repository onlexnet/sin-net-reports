package sinnet.grpc.customers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import sinnet.domain.model.ValEmail;
import sinnet.models.Clone;
import sinnet.models.ValName;

public class CustomersRpcUpdateImplTest {

    @Test
    void testNewIncomingItems() {
      var secret1 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret2 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret3 = Instancio.create(sinnet.models.CustomerSecret.class);

      var incoming = List.of(secret1, secret2, secret3);
      var persisted = List.of(secret1, secret2, secret3);
      var now = LocalDateTime.now();
      var who = ValEmail.of(UUID.randomUUID().toString());
      var updated = CustomersRpcUpdateImpl.normalizeSecrets(incoming, persisted, now, who);
      Assertions.assertThat(updated).containsExactlyElementsOf(persisted);
    }

    @Test
    void shouldDiscoverNewIncomingItems() {
      var secret1 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret2 = Instancio.create(sinnet.models.CustomerSecret.class);
      var secret3 = Instancio.create(sinnet.models.CustomerSecret.class);

      var secret2Updated = Instancio.create(sinnet.models.CustomerSecret.class);
      var now = LocalDateTime.now();
      var who = ValEmail.of(UUID.randomUUID().toString());
      var secret2normalized = Clone.INSTANCE.of(secret2Updated);
      secret2normalized.setChangedWhen(now);
      secret2normalized.setChangedWho(who);

      var incoming = List.of(secret1, secret2Updated, secret3);
      var persisted = List.of(secret1, secret2, secret3);
      var toChange = CustomersRpcUpdateImpl.normalizeSecrets(incoming, persisted, now, who);
      Assertions.assertThat(toChange).containsExactly(secret1, secret2normalized, secret3);
    }

    @Test
    void shouldRestoreTimeOnUnchangedSecrets() throws JsonProcessingException {
      var secret = Instancio.create(sinnet.models.CustomerSecret.class);
      var secretClone = Clone.INSTANCE.of(secret);
      Assertions.assertThat(secret)
        .as("We need exact clone")
        .isEqualTo(secretClone);
      
      var differentTime = secret.getChangedWhen().minus(1, ChronoUnit.YEARS);
      secretClone.setChangedWhen(differentTime);
      secretClone.setChangedWho(ValEmail.of(UUID.randomUUID().toString()));
      Assertions.assertThat(secret)
        .as("clone is change on fields which should be restored fron persistent collection")
        .isNotEqualTo(secretClone);

      Assertions.assertThat(CustomersRpcUpdateImpl.equalityComparer(secret, secretClone))
        .as("clone is equal to secret in core data fields")
        .isTrue();

      var updated = CustomersRpcUpdateImpl.normalizeSecrets(List.of(secretClone), List.of(secret), secret.getChangedWhen(), secret.getChangedWho());
      Assertions.assertThat(updated).containsExactly(secret);
    }
  }
