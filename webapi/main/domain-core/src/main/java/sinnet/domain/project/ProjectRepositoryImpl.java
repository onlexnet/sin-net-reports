package sinnet.domain.project;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.vavr.collection.Array;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import sinnet.models.ProjectId;

@Component
public class ProjectRepositoryImpl implements Project.Repository, Project {

  @Value
  @Builder
  @FieldNameConstants
  static class DboTemplate {
    private UUID entityId;
    private int entityVersion;
    private String name;
  }

  private String deleteTemplate = String.format("DELETE FROM "
      + "projects WHERE entity_id=#{%s} AND entity_version=#{%s}",
      DboTemplate.Fields.entityId, DboTemplate.Fields.entityVersion);

  private String insertTemplate = String.format("INSERT INTO "
      + "projects (entity_id, entity_version, name) "
      + "values(#{%s}, #{%s}, #{%s})",
      DboTemplate.Fields.entityId, DboTemplate.Fields.entityVersion, DboTemplate.Fields.name);

  /**
   * {@inheritDoc}
   */
  public Future<ProjectId> save(SqlClient sqlClient, UUID projectId, int version) {
    var asyncResult = Promise.<ProjectId>promise();
    var newVersion = version + 1;
    SqlTemplate
        .forUpdate(sqlClient, insertTemplate)
        .mapFrom(DboTemplate.class)
        .execute(DboTemplate.builder()
          .entityId(projectId)
          .entityVersion(newVersion)
          .build())
        .flatMap(ignored -> SqlTemplate
          .forUpdate(sqlClient, deleteTemplate)
          .mapFrom(DboTemplate.class)
          .execute(DboTemplate.builder()
            .entityId(projectId)
            .entityVersion(version)
            .build()))
        .onSuccess(ignored -> asyncResult.complete(ProjectId.of(projectId, newVersion)))
        .onFailure(asyncResult::fail);
    return asyncResult.future();
  }

  @Override
  public Future<Array<Entity>> get(SqlClient sqlClient, String filterByEmail) {
    var promise = Promise.<Array<Entity>>promise();
    var values = Tuple.of(filterByEmail);
    sqlClient
          .preparedQuery("SELECT p.entity_id, p.entity_version, p.name "
                        + "FROM projects p INNER JOIN serviceman s ON p.entity_id = s.project_entity_id "
                        + "WHERE s.email=$1")
          .execute(values)
          .onSuccess(rows -> {
            var projectsIds = Array
                .ofAll(rows)
                .map(it -> new Entity(ProjectId.of(it.getUUID(0), it.getInteger(1)), it.getString(2)));
            promise.complete(projectsIds);
          }).onFailure(promise::fail);
    return promise.future();
  }
}
