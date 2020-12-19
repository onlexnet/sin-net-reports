package sinnet.customer;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.List;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import lombok.extern.slf4j.Slf4j;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;
import sinnet.models.Name;

@Component
@Slf4j
public class CustomerRepositoryImpl implements CustomerRepository {

    private final PgPool pgClient;

    @Autowired
    public CustomerRepositoryImpl(PgPool pgclient) {
        this.pgClient = pgclient;
    }

    @Value
    @Builder
    @FieldNameConstants
    static class SaveEntry {
        private UUID projectId;
        private UUID entityId;
        private int entityVersion;
        private String operatorEmail;
        private String billingModel;
        private String supportStatus;
        private Integer distance;
        private String customerName;
        private String customerCityName;
        private String customerAddress;
    }

    private String insertTemplate = String.format("INSERT INTO "
        + "customers ("
        + "project_id, entity_id, entity_version,"
        + "customer_name, customer_city_name, customer_address,"
        + "operator_email, billing_model, support_status, distance"
        + ") "
        + "VALUES (#{%s}, #{%s}, #{%s}+1,"
        + "#{%s}, #{%s}, #{%s},"
        + "#{%s}, #{%s}, #{%s}, #{%s})",
        SaveEntry.Fields.projectId, SaveEntry.Fields.entityId, SaveEntry.Fields.entityVersion,
        SaveEntry.Fields.customerName, SaveEntry.Fields.customerCityName, SaveEntry.Fields.customerAddress,
        SaveEntry.Fields.operatorEmail, SaveEntry.Fields.billingModel, SaveEntry.Fields.supportStatus, SaveEntry.Fields.distance
        );
    private String deleteTemplate = String.format("DELETE FROM "
        + "customers WHERE project_id=#{%s} AND entity_id=#{%s} AND entity_version=#{%s}",
        SaveEntry.Fields.projectId, SaveEntry.Fields.entityId, SaveEntry.Fields.entityVersion);

    public Future<EntityId> save(EntityId id, CustomerValue entity) {
        var entry = SaveEntry.builder()
            .projectId(id.getProjectId())
            .entityId(id.getId())
            .entityVersion(id.getVersion())
            .operatorEmail(entity.getOperatorEmail())
            .billingModel(entity.getBillingModel())
            .supportStatus(entity.getSupportStatus())
            .distance(entity.getDistance())
            .customerName(entity.getCustomerName().getValue())
            .customerCityName(entity.getCustomerCityName().getValue())
            .customerAddress(entity.getCustomerAddress())
            .build();
        return pgClient.withTransaction(client -> SqlTemplate
                .forUpdate(client, insertTemplate)
                .mapFrom(SaveEntry.class)
                .execute(entry)
                .flatMap(res -> SqlTemplate
                    .forUpdate(client, deleteTemplate)
                    .mapFrom(SaveEntry.class)
                    .execute(entry)
                    .map(EntityId.of(id.getProjectId(), id.getId(), id.getVersion() + 1)))
        );
    }

    private final Exception noDataException = new Exception("No data");
    private Future<Entity<CustomerValue>> extractResult(Iterable<Entity<CustomerValue>> candidates) {
        var iter = candidates.iterator();
        if (!iter.hasNext()) return Future.failedFuture(noDataException);
        var item = iter.next();
        return Future.succeededFuture(item);
    }

    @Override
    public Future<Entity<CustomerValue>> get(EntityId id) {
        var whereClause = "project_id=$1 AND entity_id=$2 AND entity_version=$3";
        var values = Tuple.of(id.getProjectId(), id.getId(), id.getVersion());
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<Entity<CustomerValue>> get(UUID projectId, UUID id) {
        var whereClause = "project_id=$1 AND entity_id=$2";
        var values = Tuple.of(projectId, id);
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<Iterable<Entity<CustomerValue>>> list(UUID projectId) {
        return get("project_id=$1", Tuple.of(projectId))
            .map(it -> it);
    }

    private Future<List<Entity<CustomerValue>>> get(String whereClause, Tuple values) {
        var promise = Promise.<List<Entity<CustomerValue>>>promise();
        pgClient
            .preparedQuery("SELECT "
                         + "project_id, entity_id, entity_version, "
                         + "customer_name, customer_city_name, customer_address, "
                         + "operator_email, billing_model, support_status, distance "
                         + "FROM customers c "
                         + "WHERE " + whereClause)
            .execute(values, ar -> {
                if (ar.succeeded()) {
                    var result = List.<Entity<CustomerValue>>empty();
                    var iter = ar.result();
                    for (var row: iter) {
                        var projectId = row.getUUID("project_id");
                        var entityId = row.getUUID("entity_id");
                        var entityVersion = row.getInteger("entity_version");
                        var item = CustomerValue.builder()
                            .customerName(Name.of(row.getString("customer_name")))
                            .customerCityName(Name.of(row.getString("customer_city_name")))
                            .customerAddress(row.getString("customer_address"))
                            .operatorEmail(row.getString("operator_email"))
                            .billingModel(row.getString("billing_model"))
                            .supportStatus(row.getString("support_status"))
                            .distance(row.getInteger("distance"))
                            .build()
                            .withId(projectId, entityId, entityVersion);
                        result = result.append(item);
                    }
                    promise.complete(result);
                } else {
                    log.error("CustomerRepositoryImpl.get", ar.cause());
                    promise.complete(null);
                }
            });
        return promise.future();
    }
}
