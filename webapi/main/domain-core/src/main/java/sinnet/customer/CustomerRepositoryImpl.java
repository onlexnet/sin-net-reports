package sinnet.customer;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

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
import sinnet.models.CustomerAuthorization;
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
        private boolean nfzUmowa;
        private boolean nfzMaFilie;
        private boolean nfzLekarz;
        private boolean nfzPolozna;
        private boolean nfzPielegniarkaSrodowiskowa;
        private boolean nfzMedycynaSzkolna;
        private boolean nfzTransportSanitarny;
        private boolean nfzNocnaPomocLekarska;
        private boolean nfzAmbulatoryjnaOpiekaSpecjalistyczna;
        private boolean nfzRehabilitacja;
        private boolean nfzStomatologia;
        private boolean nfzPsychiatria;
        private boolean nfzSzpitalnictwo;
        private boolean nfzProgramyProfilaktyczne;
        private boolean nfzZaopatrzenieOrtopedyczne;
        private boolean nfzOpiekaDlugoterminowa;
        private String nfzNotatki;
        private boolean komercjaJest;
        private String komercjaNotatki;
    }

    private String deleteTemplate = String.format("DELETE FROM "
        + "customers WHERE project_id=#{%s} AND entity_id=#{%s} AND entity_version=#{%s}",
        SaveEntry.Fields.projectId, SaveEntry.Fields.entityId, SaveEntry.Fields.entityVersion);

    private String insertTemplate = String.format("INSERT INTO "
        + "customers ("
        + "project_id, entity_id, entity_version,"
        + "customer_name, customer_city_name, customer_address,"
        + "operator_email, billing_model, support_status, distance,"
        + "nfz_umowa, nfz_ma_filie, nfz_lekarz, nfz_polozna, "
        + "nfz_pielegniarka_srodowiskowa, nfz_medycyna_szkolna, nfz_transport_sanitarny, nfz_nocna_pomoc_lekarska, "
        + "nfz_ambulatoryjna_opieka_specjalistyczna, nfz_rehabilitacja, nfz_stomatologia, nfz_psychiatria, "
        + "nfz_szpitalnictwo, nfz_programy_profilaktyczne, nfz_zaopatrzenie_ortopedyczne, nfz_opieka_dlugoterminowa, "
        + "nfz_notatki,"
        + "komercja_jest, komercja_notatki"
        + ") "
        + "VALUES ("
        + "#{%s}, #{%s}, #{%s}+1"
        + ",#{%s}, #{%s}, #{%s}"
        + ",#{%s}, #{%s}, #{%s}, #{%s}"
        + ",#{%s}, #{%s}, #{%s}, #{%s}"
        + ",#{%s}, #{%s}, #{%s}, #{%s}"
        + ",#{%s}, #{%s}, #{%s}, #{%s}"
        + ",#{%s}, #{%s}, #{%s}, #{%s}"
        + ",#{%s}"
        + ",#{%s}, #{%s}"
        + ")",
        SaveEntry.Fields.projectId, SaveEntry.Fields.entityId, SaveEntry.Fields.entityVersion,
        SaveEntry.Fields.customerName, SaveEntry.Fields.customerCityName, SaveEntry.Fields.customerAddress,
        SaveEntry.Fields.operatorEmail, SaveEntry.Fields.billingModel, SaveEntry.Fields.supportStatus, SaveEntry.Fields.distance,
        SaveEntry.Fields.nfzUmowa, SaveEntry.Fields.nfzMaFilie, SaveEntry.Fields.nfzLekarz, SaveEntry.Fields.nfzPolozna,
        SaveEntry.Fields.nfzPielegniarkaSrodowiskowa, SaveEntry.Fields.nfzMedycynaSzkolna, SaveEntry.Fields.nfzTransportSanitarny,
            SaveEntry.Fields.nfzNocnaPomocLekarska,
        SaveEntry.Fields.nfzAmbulatoryjnaOpiekaSpecjalistyczna, SaveEntry.Fields.nfzRehabilitacja, SaveEntry.Fields.nfzStomatologia,
            SaveEntry.Fields.nfzPsychiatria,
        SaveEntry.Fields.nfzSzpitalnictwo, SaveEntry.Fields.nfzProgramyProfilaktyczne, SaveEntry.Fields.nfzZaopatrzenieOrtopedyczne,
            SaveEntry.Fields.nfzOpiekaDlugoterminowa,
        SaveEntry.Fields.nfzNotatki,
        SaveEntry.Fields.komercjaJest, SaveEntry.Fields.komercjaNotatki);

    @Value
    @Builder
    @FieldNameConstants
    static class SaveAuthEntry {
        private UUID customerId;
        private String location;
        private String username;
        private String password;
    }
    private String insertAuthTemplate = String.format(
        "INSERT INTO \"authorization\" "
        + "("
        + "customer_id, location, username, password"
        + ") "
        + "VALUES ("
        + "#{%s}, #{%s}, #{%s}, #{%s}"
        + ")",
        SaveAuthEntry.Fields.customerId,
        SaveAuthEntry.Fields.location,
        SaveAuthEntry.Fields.username,
        SaveAuthEntry.Fields.password);

    public Future<EntityId> save(EntityId id, CustomerValue entity, CustomerAuthorization[] auth) {
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
            .nfzUmowa(entity.isNfzUmowa())
            .nfzMaFilie(entity.isNfzMaFilie())
            .nfzLekarz(entity.isNfzLekarz())
            .nfzPolozna(entity.isNfzPolozna())
            .nfzPielegniarkaSrodowiskowa(entity.isNfzPielegniarkaSrodowiskowa())
            .nfzMedycynaSzkolna(entity.isNfzMedycynaSzkolna())
            .nfzTransportSanitarny(entity.isNfzTransportSanitarny())
            .nfzNocnaPomocLekarska(entity.isNfzNocnaPomocLekarska())
            .nfzAmbulatoryjnaOpiekaSpecjalistyczna(entity.isNfzAmbulatoryjnaOpiekaSpecjalistyczna())
            .nfzRehabilitacja(entity.isNfzRehabilitacja())
            .nfzStomatologia(entity.isNfzStomatologia())
            .nfzPsychiatria(entity.isNfzPsychiatria())
            .nfzSzpitalnictwo(entity.isNfzSzpitalnictwo())
            .nfzProgramyProfilaktyczne(entity.isNfzProgramyProfilaktyczne())
            .nfzZaopatrzenieOrtopedyczne(entity.isNfzZaopatrzenieOrtopedyczne())
            .nfzOpiekaDlugoterminowa(entity.isNfzOpiekaDlugoterminowa())
            .nfzNotatki(entity.getNfzNotatki())
            .komercjaJest(entity.isKomercjaJest())
            .komercjaNotatki(entity.getKomercjaNotatki())
            .build();
        var authBatch = Arrays.stream(auth)
            .map(it -> SaveAuthEntry.builder()
                                   .customerId(id.getId())
                                   .location(it.getLocation())
                                   .username(it.getUsername())
                                   .password(it.getPassword())
                                   .build())
            .collect(Collectors.toList());
        return pgClient.withTransaction(client -> SqlTemplate
                .forUpdate(client, deleteTemplate)
                .mapFrom(SaveEntry.class)
                .execute(entry)
                .flatMap(res -> SqlTemplate
                    .forUpdate(client, insertTemplate)
                    .mapFrom(SaveEntry.class)
                    .execute(entry))
                .map(res -> {
                    if (authBatch.isEmpty()) return Boolean.TRUE;
                    SqlTemplate
                        .forUpdate(client, insertAuthTemplate)
                        .mapFrom(SaveAuthEntry.class)
                        .executeBatch(authBatch);
                    return Boolean.TRUE; })
                .map(ignored -> EntityId.of(id.getProjectId(), id.getId(), id.getVersion() + 1)));
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
                         + "operator_email, billing_model, support_status, distance, "
                         + "nfz_umowa, nfz_ma_filie, nfz_lekarz, nfz_polozna, "
                         + "nfz_pielegniarka_srodowiskowa, nfz_medycyna_szkolna, nfz_transport_sanitarny, nfz_nocna_pomoc_lekarska, "
                         + "nfz_ambulatoryjna_opieka_specjalistyczna, nfz_rehabilitacja, nfz_stomatologia, nfz_psychiatria, "
                         + "nfz_szpitalnictwo, nfz_programy_profilaktyczne, nfz_zaopatrzenie_ortopedyczne, nfz_opieka_dlugoterminowa, "
                         + "nfz_notatki, "
                         + "komercja_jest, komercja_notatki "
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
                            .nfzUmowa(row.getBoolean("nfz_umowa"))
                            .nfzMaFilie(row.getBoolean("nfz_ma_filie"))
                            .nfzLekarz(row.getBoolean("nfz_lekarz"))
                            .nfzPolozna(row.getBoolean("nfz_polozna"))
                            .nfzPielegniarkaSrodowiskowa(row.getBoolean("nfz_pielegniarka_srodowiskowa"))
                            .nfzMedycynaSzkolna(row.getBoolean("nfz_medycyna_szkolna"))
                            .nfzTransportSanitarny(row.getBoolean("nfz_transport_sanitarny"))
                            .nfzNocnaPomocLekarska(row.getBoolean("nfz_nocna_pomoc_lekarska"))
                            .nfzAmbulatoryjnaOpiekaSpecjalistyczna(row.getBoolean("nfz_ambulatoryjna_opieka_specjalistyczna"))
                            .nfzRehabilitacja(row.getBoolean("nfz_rehabilitacja"))
                            .nfzStomatologia(row.getBoolean("nfz_stomatologia"))
                            .nfzPsychiatria(row.getBoolean("nfz_psychiatria"))
                            .nfzSzpitalnictwo(row.getBoolean("nfz_szpitalnictwo"))
                            .nfzProgramyProfilaktyczne(row.getBoolean("nfz_programy_profilaktyczne"))
                            .nfzZaopatrzenieOrtopedyczne(row.getBoolean("nfz_zaopatrzenie_ortopedyczne"))
                            .nfzOpiekaDlugoterminowa(row.getBoolean("nfz_opieka_dlugoterminowa"))
                            .nfzNotatki(row.getString("nfz_notatki"))
                            .komercjaJest(row.getBoolean("komercja_jest"))
                            .komercjaNotatki(row.getString("komercja_notatki"))
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
