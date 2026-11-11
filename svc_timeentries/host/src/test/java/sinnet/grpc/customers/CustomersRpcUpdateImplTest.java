package sinnet.grpc.customers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import sinnet.domain.model.ValEmail;
import sinnet.models.Clone;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import sinnet.models.CustomerModel;
import sinnet.models.ShardedId;

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

    @Test
    void shouldKeepStandardMetadataWithinItsGroup() {
      var sinnetSecret = Instancio.create(sinnet.models.CustomerSecret.class)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      var customer = Clone.INSTANCE.of(sinnetSecret)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER)
          .setChangedWhen(sinnetSecret.getChangedWhen().minusDays(1))
          .setChangedWho(ValEmail.of("customer@example.test"));
      var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      var requestor = ValEmail.of("saving@example.test");
      var added = Clone.INSTANCE.of(customer).setPassword("changed");
      var expectedAdded = Clone.INSTANCE.of(added).setChangedWhen(now).setChangedWho(requestor);

      var actual = CustomersRpcUpdateImpl.normalizeSecrets(
          List.of(Clone.INSTANCE.of(customer), added, Clone.INSTANCE.of(sinnetSecret)),
          List.of(sinnetSecret, customer), now, requestor);

      Assertions.assertThat(actual).containsExactly(customer, expectedAdded, sinnetSecret);
    }

    @Test
    void shouldTreatIdenticalStandardCredentialsInAnotherGroupAsNew() {
      var sinnetSecret = Instancio.create(sinnet.models.CustomerSecret.class)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      var incoming = Clone.INSTANCE.of(sinnetSecret)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER);
      var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      var requestor = ValEmail.of("saving@example.test");
      var expected = Clone.INSTANCE.of(incoming).setChangedWhen(now).setChangedWho(requestor);
      Assertions.assertThat(CustomersRpcUpdateImpl.normalizeSecrets(
          List.of(incoming), List.of(sinnetSecret), now, requestor)).containsExactly(expected);
    }

    @Test
    void shouldKeepExtendedMetadataWithinItsGroup() {
      var sinnetSecret = Instancio.create(sinnet.models.CustomerSecretEx.class)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      var customer = Clone.INSTANCE.of(sinnetSecret)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER)
          .setChangedWhen(sinnetSecret.getChangedWhen().minusDays(1))
          .setChangedWho(ValEmail.of("customer@example.test"));
      var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      var requestor = ValEmail.of("saving@example.test");
      var added = Clone.INSTANCE.of(customer).setEntityCode("changed");
      var expectedAdded = Clone.INSTANCE.of(added).setChangedWhen(now).setChangedWho(requestor);

      var actual = CustomersRpcUpdateImpl.normalizeSecretsExt(
          List.of(Clone.INSTANCE.of(customer), added, Clone.INSTANCE.of(sinnetSecret)),
          List.of(sinnetSecret, customer), now, requestor);

      Assertions.assertThat(actual).containsExactly(customer, expectedAdded, sinnetSecret);
    }

    @Test
    void shouldTreatIdenticalExtendedCredentialsInAnotherGroupAsNew() {
      var sinnetSecret = Instancio.create(sinnet.models.CustomerSecretEx.class)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      var incoming = Clone.INSTANCE.of(sinnetSecret)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER);
      var now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      var requestor = ValEmail.of("saving@example.test");
      var expected = Clone.INSTANCE.of(incoming).setChangedWhen(now).setChangedWho(requestor);
      Assertions.assertThat(CustomersRpcUpdateImpl.normalizeSecretsExt(
          List.of(incoming), List.of(sinnetSecret), now, requestor)).containsExactly(expected);
    }

    @Test
    void shouldStampNewCustomerCredentialsInBothGroups() {
      var model = Instancio.of(CustomerModel.class)
          .supply(org.instancio.Select.all(LocalDateTime.class),
              () -> LocalDateTime.of(2020, 1, 2, 3, 4))
          .create();
      model.setId(ShardedId.of(UUID.randomUUID(), UUID.randomUUID(), -1));
      var standard = model.getSecrets().getFirst()
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      var extended = model.getSecretsEx().getFirst()
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.SINNET);
      model.setSecrets(List.of(standard, Clone.INSTANCE.of(standard)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER)));
      model.setSecretsEx(List.of(extended, Clone.INSTANCE.of(extended)
          .setAuthorizationGroup(sinnet.models.AuthorizationGroup.CUSTOMER)));
      var repository = Mockito.mock(CustomerRepositoryEx.class);
      Mockito.when(repository.write(Mockito.any())).thenReturn(model.getId().next());
      var before = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
      new CustomersRpcUpdateImpl(repository).apply(UpdateCommand.newBuilder()
          .setModel(MapperDto.toDto(model))
          .setUserToken(sinnet.grpc.common.UserToken.newBuilder().setRequestorEmail("saving@example.test"))
          .build());
      var saved = ArgumentCaptor.forClass(CustomerModel.class);
      Mockito.verify(repository).write(saved.capture());
      var actual = saved.getValue();
      var changedAt = actual.getSecrets().getFirst().getChangedWhen();
      Assertions.assertThat(changedAt).isBetween(before, LocalDateTime.now());
      model.getSecrets().forEach(it -> it.setChangedWhen(changedAt)
          .setChangedWho(ValEmail.of("saving@example.test")));
      model.getSecretsEx().forEach(it -> it.setChangedWhen(changedAt)
          .setChangedWho(ValEmail.of("saving@example.test")));
      Assertions.assertThat(actual).isEqualTo(model);
    }
  }
