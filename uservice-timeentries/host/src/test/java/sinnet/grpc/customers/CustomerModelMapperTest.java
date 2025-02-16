package sinnet.grpc.customers;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;

import sinnet.grpc.common.EntityId;
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

    var expected = new ShardedId(projectId, entityId, 3);
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
  void shouldMapCustomerModel() {
    var dto = Instancio.of(CustomerModel.class)
        .generate(Select.field(LocalDateTime.class, "month"), gen -> gen.ints().range(1, 12))
        .create();
    var model = mapper.fromDto(dto);
    Assertions.assertThat(model).isNotNull();
    throw new IllegalArgumentException("Test is not yet finished");
  }
}
