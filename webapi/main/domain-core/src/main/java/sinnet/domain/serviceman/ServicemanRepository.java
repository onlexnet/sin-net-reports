package sinnet.domain.serviceman;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import sinnet.models.EntityId;

/** Allows to write / read Serviceman entity. */
@Component
public class ServicemanRepository {

  @Value
  @Builder
  @FieldNameConstants
  static class DboEntry {
    private UUID entityId;
    private int entityVersion;
    private UUID projectEntityId;
    private String email;
    private String customName;
  }

  private String writeSql = "INSERT INTO "
      + "serviceman ("
      + "project_entity_id, entity_id, entity_version, email, custom_name"
      + ")"
      + " VALUES ("
      + "#{" + DboEntry.Fields.projectEntityId + "}, "
      + "#{" + DboEntry.Fields.entityId + "}, "
      + "#{" + DboEntry.Fields.entityVersion + "}, "
      + "#{" + DboEntry.Fields.email + "}, "
      + "#{" + DboEntry.Fields.customName + "})";

  Future<EntityId> write(SqlClient sqlClient, ServicemanState state) {
    var projectEntityId = state.getEntityId().getProjectId();
    var entityId = state.getEntityId().getId();
    var version = state.getEntityId().getVersion();
    var newVersion = version + 1;
    var entry = DboEntry.builder()
        .entityId(entityId)
        .entityVersion(newVersion)
        .projectEntityId(projectEntityId)
        .email(state.getEmail().getValue())
        .customName(state.getName())
        .build();
    return SqlTemplate
      .forUpdate(sqlClient, writeSql)
      .mapFrom(DboEntry.class)
      .execute(entry)
      .map(ignored -> EntityId.of(projectEntityId, entityId, newVersion));
  }
}
