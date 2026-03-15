package sinnet.infra.adapters.gql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sinnet.domain.models.EntityId;
import sinnet.gql.models.EntityGql;
import sinnet.infra.adapters.gql.EntityGqlMapper;

@DisplayName("EntityGqlMapper Tests")
class EntityGqlMapperTest {

  private final EntityGqlMapper mapper = EntityGqlMapper.INSTANCE;

  @Test
  @DisplayName("should map domain EntityId to gql EntityGql")
  void shouldMapDomainToGql() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    var source = new EntityId(projectId, entityId, 42L);

    var result = mapper.toGql(source);

    assertThat(result).isEqualTo(new EntityGql(projectId.toString(), entityId.toString(), 42L));
  }

  @Test
  @DisplayName("should map gql EntityGql to domain EntityId")
  void shouldMapGqlToDomain() {
    var projectId = UUID.randomUUID();
    var entityId = UUID.randomUUID();

    var source = new EntityGql(projectId.toString(), entityId.toString(), 9L);

    var result = mapper.toDomain(source);

    assertThat(result).isEqualTo(new EntityId(projectId, entityId, 9L));
  }

  @Test
  @DisplayName("should map empty gql ids to null domain UUIDs")
  void shouldMapEmptyStringsToNullUuids() {
    var source = new EntityGql("", "", 7L);

    var result = mapper.toDomain(source);

    assertThat(result).isEqualTo(new EntityId(null, null, 7L));
  }
}
