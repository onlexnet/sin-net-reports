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
}
