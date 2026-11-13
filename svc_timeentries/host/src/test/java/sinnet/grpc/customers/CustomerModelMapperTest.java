package sinnet.grpc.customers;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import sinnet.grpc.common.EntityId;
import sinnet.models.EntityVersion;
import sinnet.models.ShardedId;
import sinnet.models.Clone;

public class CustomerModelMapperTest {

  static CustomerModelMapper mapper = CustomerModelMapper.INSTANCE;

  @Test
  void shouldMapShardedId() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    var dto = EntityId.newBuilder()
        .setProjectId(projectId.toString())
        .setEntityId(entityId.toString())
        .setEntityVersion(3)
        .build();
    var actual = CustomerModelMapper.INSTANCE.fromDto(dto);

    var expected = new ShardedId(projectId, entityId, EntityVersion.of(3));
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldMapLocalDateTime() {

    var expected = java.time.LocalDateTime.of(2001, 2, 3, 4, 5, 6);
    var asDto = mapper.toDto(expected);
    var actual = mapper.fromDto(asDto);

    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldMapCustomerModelFull() {
    var expected = Instancio.of(sinnet.models.CustomerModel.class)
        .supply(Select.all(java.time.LocalDateTime.class), () -> java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
        .create();
    var dto = mapper.toDto(expected);
    var actual = mapper.fromDto(dto);
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldMapCustomerModelEmpty() {
    var expected = Instancio.createBlank(sinnet.models.CustomerModel.class);
    var dto = mapper.toDto(expected);
    var actual = mapper.fromDto(dto);
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  void shouldPreserveGroupsAcrossManualAndGeneratedMappings() {
    for (var group : sinnet.models.AuthorizationGroup.values()) {
      var expected = Instancio.of(sinnet.models.CustomerModel.class)
          .supply(Select.all(java.time.LocalDateTime.class),
              () -> java.time.LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
          .create();
      expected.setId(new ShardedId(expected.getId().projectId(), expected.getId().id(), EntityVersion.of(0)));
      expected.getSecrets().forEach(it -> it.setAuthorizationGroup(group));
      expected.getSecretsEx().forEach(it -> it.setAuthorizationGroup(group));
      var repository = new CustomerRepositoryEx(org.mockito.Mockito.mock(CustomerRepository.class));
      var dbo = CustomerMapper.INSTANCE.toJpaDbo(expected);
      var results = java.util.List.of(
          mapper.fromDto(mapper.toDto(expected)),
          MapperDto.fromDto(MapperDto.toDto(expected)),
          CustomerMapper.INSTANCE.fromDbo2(dbo),
          repository.fromDbo(dbo));
      Assertions.assertThat(results).allSatisfy(actual ->
          Assertions.assertThat(actual).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(expected));
      for (var secret : expected.getSecrets()) {
        Assertions.assertThat(repository.fromDbo(repository.toDbo(secret))).isEqualTo(secret);
        Assertions.assertThat(Clone.INSTANCE.of(secret)).isEqualTo(secret);
      }
      for (var secret : expected.getSecretsEx()) {
        Assertions.assertThat(repository.fromDbo(repository.toDbo(secret))).isEqualTo(secret);
        Assertions.assertThat(Clone.INSTANCE.of(secret)).isEqualTo(secret);
      }
    }
  }

  @Test
  void shouldDefaultLegacyPayloadsAndRejectUnknownGroups() {
    var time = MapperDto.toDto(java.time.LocalDateTime.of(2020, 1, 2, 3, 4));
    Assertions.assertThat(MapperDto.fromDto(
        CustomerSecret.newBuilder().setChangedWhen(time).build()).getAuthorizationGroup())
        .isEqualTo(sinnet.models.AuthorizationGroup.SINNET);
    Assertions.assertThat(MapperDto.fromDto(
        CustomerSecretEx.newBuilder().setChangedWhen(time).build()).getAuthorizationGroup())
        .isEqualTo(sinnet.models.AuthorizationGroup.SINNET);
    Assertions.assertThatThrownBy(() -> MapperDto.fromDto(
        CustomerSecret.newBuilder().setAuthorizationGroupValue(99).build()))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> MapperDto.fromDto(
        CustomerSecretEx.newBuilder().setAuthorizationGroupValue(99).build()))
        .isInstanceOf(IllegalArgumentException.class);
    Assertions.assertThatThrownBy(() -> mapper.fromDto(AuthorizationGroup.UNRECOGNIZED))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
