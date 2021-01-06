package sinnet.customer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.templates.TupleMapper;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldNameConstants;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.Entity;
import sinnet.models.EntityId;
import sinnet.models.Name;

@Component
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
        private String daneTechniczne;
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
        + "komercja_jest, komercja_notatki, dane_techniczne"
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
        + ",#{%s}, #{%s}, #{%s}"
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
        SaveEntry.Fields.komercjaJest, SaveEntry.Fields.komercjaNotatki, SaveEntry.Fields.daneTechniczne);

    @Value
    @Builder
    @FieldNameConstants
    static class SaveSecretEntry {
        private UUID customerId;
        private String location;
        private String username;
        private String password;
        private String changedWho;
        private LocalDateTime changedWhen;
    }
    private String insertSecretTemplate = String.format(
        "INSERT INTO secret "
        + "("
        + "customer_id, location, username, password, changed_who, changed_when"
        + ") "
        + "VALUES ("
        + "#{%s}, #{%s}, #{%s}, #{%s}, #{%s}, #{%s}"
        + ")",
        SaveSecretEntry.Fields.customerId,
        SaveSecretEntry.Fields.location,
        SaveSecretEntry.Fields.username,
        SaveSecretEntry.Fields.password,
        SaveSecretEntry.Fields.changedWho,
        SaveSecretEntry.Fields.changedWhen);

    @Value
    @Builder
    @FieldNameConstants
    static class SaveSecretExEntry {
        private UUID customerId;
        private String location;
        private String username;
        private String password;
        private String entityName;
        private String entityCode;
        private String changedWho;
        private LocalDateTime changedWhen;
}
    private String insertSecretExTemplate = String.format(
        "INSERT INTO secret_ex "
        + "("
        + "customer_id, location, username, password, entity_name, entity_code, changed_who, changed_when"
        + ") "
        + "VALUES ("
        + "#{%s}, #{%s}, #{%s}, #{%s}, #{%s}, #{%s}, #{%s}, #{%s}"
        + ")",
        SaveSecretExEntry.Fields.customerId,
        SaveSecretExEntry.Fields.location,
        SaveSecretExEntry.Fields.username,
        SaveSecretExEntry.Fields.password,
        SaveSecretExEntry.Fields.entityName,
        SaveSecretExEntry.Fields.entityCode,
        SaveSecretEntry.Fields.changedWho,
        SaveSecretEntry.Fields.changedWhen);

    @Value
    @Builder
    @FieldNameConstants
    static class SaveContactEntry {
        private UUID customerId;
        private String firstName;
        private String lastName;
        private String phoneNo;
        private String email;
    }
    private String insertContactTemplate = String.format(
        "INSERT INTO contact "
        + "("
        + "customer_id, first_name, last_name, phone_no, email"
        + ") "
        + "VALUES ("
        + "#{%s}, #{%s}, #{%s}, #{%s}, #{%s}"
        + ")",
        SaveContactEntry.Fields.customerId,
        SaveContactEntry.Fields.firstName,
        SaveContactEntry.Fields.lastName,
        SaveContactEntry.Fields.phoneNo,
        SaveContactEntry.Fields.email);

    @Override
    public Future<EntityId> write(EntityId id, CustomerValue entity,
                                               CustomerSecret[] secrets,
                                               CustomerSecretEx[] secretsEx,
                                               CustomerContact[] contacts) {
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
            .daneTechniczne(entity.getDaneTechniczne())
            .build();
        var secretBatch = Arrays.stream(secrets)
            .map(it -> new JsonObject()
                .put(SaveSecretEntry.Fields.customerId, id.getId())
                .put(SaveSecretEntry.Fields.location, it.getLocation())
                .put(SaveSecretEntry.Fields.username, it.getUsername())
                .put(SaveSecretEntry.Fields.password, it.getPassword())
                .put(SaveSecretEntry.Fields.changedWho, it.getChangedWho().getValue())
                .put(SaveSecretEntry.Fields.changedWhen, it.getChangedWhen()))
            .collect(Collectors.toList());
        var secretExBatch = Arrays.stream(secretsEx)
            .map(it -> new JsonObject()
                .put(SaveSecretExEntry.Fields.customerId, id.getId())
                .put(SaveSecretExEntry.Fields.location, it.getLocation())
                .put(SaveSecretExEntry.Fields.username, it.getUsername())
                .put(SaveSecretExEntry.Fields.password, it.getPassword())
                .put(SaveSecretExEntry.Fields.entityName, it.getEntityName())
                .put(SaveSecretExEntry.Fields.entityCode, it.getEntityCode())
                .put(SaveSecretExEntry.Fields.changedWho, it.getChangedWho().getValue())
                .put(SaveSecretExEntry.Fields.changedWhen, it.getChangedWhen()))
            .collect(Collectors.toList());
        var contactBatch = Arrays.stream(contacts)
            .map(it -> SaveContactEntry
                .builder()
                .customerId(id.getId())
                .firstName(it.getFirstName())
                .lastName(it.getLastName())
                .phoneNo(it.getPhoneNo())
                .email(it.getEmail())
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
                .flatMap(res -> {
                    if (secretBatch.isEmpty()) return Future.succeededFuture(Boolean.TRUE);
                    return SqlTemplate
                        .forUpdate(client, insertSecretTemplate)
                        .mapFrom(TupleMapper.jsonObject())
                        .executeBatch(secretBatch)
                        .map(ignored -> Boolean.TRUE);
                })
                .flatMap(res -> {
                    if (secretExBatch.isEmpty()) return Future.succeededFuture(Boolean.TRUE);
                    return SqlTemplate
                        .forUpdate(client, insertSecretExTemplate)
                        .mapFrom(TupleMapper.jsonObject())
                        .executeBatch(secretExBatch)
                        .map(ignored -> Boolean.TRUE);
                })
                .flatMap(res -> {
                    if (contactBatch.isEmpty()) return Future.succeededFuture(Boolean.TRUE);
                    return SqlTemplate
                        .forUpdate(client, insertContactTemplate)
                        .mapFrom(SaveContactEntry.class)
                        .executeBatch(contactBatch)
                        .map(ignored -> Boolean.TRUE);
                })
                .map(ignored -> EntityId.of(id.getProjectId(), id.getId(), id.getVersion() + 1)));
    }

    private Future<Option<CustomerModel>> extractResult(Iterable<CustomerModel> candidates) {
        var iter = candidates.iterator();
        var result = iter.hasNext()
                   ? Option.of(iter.next())
                   : Option.<CustomerModel>none();
        return Future.succeededFuture(result);
    }


    @Override
    public Future<Boolean> remove(EntityId id) {
        var params = new JsonObject()
            .put("project_id", id.getProjectId())
            .put("entity_id", id.getId())
            .put("entity_version", id.getVersion());
        return SqlTemplate
            .forQuery(pgClient, "DELETE "
                + "FROM customers "
                + "WHERE project_id=#{project_id} "
                + "AND entity_id=#{entity_id} "
                + "AND entity_version=#{entity_version}")
            .mapFrom(TupleMapper.jsonObject())
            .execute(params)
            .map(v -> {
                return Boolean.TRUE;
            });
    }

    @Override
    public Future<Option<CustomerModel>> get(EntityId id) {
        var whereClause = "project_id=$1 AND entity_id=$2 AND entity_version=$3";
        var values = Tuple.of(id.getProjectId(), id.getId(), id.getVersion());
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<Option<CustomerModel>> get(UUID projectId, UUID id) {
        var whereClause = "project_id=$1 AND entity_id=$2";
        var values = Tuple.of(projectId, id);
        return get(whereClause, values)
            .map(this::extractResult)
            .flatMap(it -> it);
    }

    @Override
    public Future<List<CustomerModel>> list(UUID projectId) {
        return get("project_id=$1", Tuple.of(projectId))
            .map(it -> it);

    }

    /** Reusable method to map database to domain model. */
    private Future<List<CustomerModel>> get(String whereClause, Tuple values) {
        return pgClient.withTransaction(client ->
            client.preparedQuery("SELECT "
                + "project_id, entity_id, entity_version, "
                + "customer_name, customer_city_name, customer_address, "
                + "operator_email, billing_model, support_status, distance, "
                + "nfz_umowa, nfz_ma_filie, nfz_lekarz, nfz_polozna, "
                + "nfz_pielegniarka_srodowiskowa, nfz_medycyna_szkolna, nfz_transport_sanitarny, nfz_nocna_pomoc_lekarska, "
                + "nfz_ambulatoryjna_opieka_specjalistyczna, nfz_rehabilitacja, nfz_stomatologia, nfz_psychiatria, "
                + "nfz_szpitalnictwo, nfz_programy_profilaktyczne, nfz_zaopatrzenie_ortopedyczne, nfz_opieka_dlugoterminowa, "
                + "nfz_notatki, "
                + "komercja_jest, komercja_notatki, dane_techniczne "
                + "FROM customers c "
                + "WHERE " + whereClause)
                .execute(values)
                .flatMap(ar -> {
                    var customers = List.ofAll(ar)
                        .map(row -> toCustomerEntity(row));
                    var ids = customers
                        .map(it -> it.getEntityId())
                        .map(it -> Collections.singletonMap("cid", (Object) it))
                        .toJavaList();
                    if (ids.isEmpty()) return Future.succeededFuture(List.empty());

                    var secrets = SqlTemplate
                        .forQuery(client, "SELECT customer_id, location, username, password, changed_who, changed_when "
                                          + "FROM secret WHERE customer_id=#{cid}")
                        .executeBatch(ids)
                        .map(rows -> List
                            .ofAll(rows)
                            .map(row -> toCustomerSecret(row)));
                    var secretsEx = SqlTemplate
                        .forQuery(client, "SELECT customer_id, location, username, password, entity_name, entity_code, changed_who, changed_when "
                                          + "FROM secret_ex WHERE customer_id=#{cid}")
                        .executeBatch(ids)
                        .map(rows -> List
                            .ofAll(rows)
                            .map(row -> toCustomerSecretEx(row)));
                    var contacts = SqlTemplate
                        .forQuery(client, "SELECT customer_id, first_name, last_name, phone_no, email "
                                          + "FROM contact WHERE customer_id=#{cid}")
                        .executeBatch(ids)
                        .map(rows -> List
                            .ofAll(rows)
                            .map(row -> toCustomerContact(row)));

                    return CompositeFuture
                        .all(secrets, secretsEx, contacts)
                        .map(v -> {
                            List<Tuple2<UUID, CustomerSecret>> v1 = v.resultAt(0);
                            List<Tuple2<UUID, CustomerSecretEx>> v2 = v.resultAt(1);
                            List<Tuple2<UUID, CustomerContact>> v3 = v.resultAt(2);
                            return finalMapping(customers, v1, v2, v3);
                        });
                })
        );
    }

    static List<CustomerModel> finalMapping(List<Entity<CustomerValue>> customers,
                                            List<Tuple2<UUID, CustomerSecret>> secrets,
                                            List<Tuple2<UUID, CustomerSecretEx>> secretsEx,
                                            List<Tuple2<UUID, CustomerContact>> contacts) {
        var groupedSecrets = secrets.groupBy(it -> it._1);
        var groupedSecretsEx = secretsEx.groupBy(it -> it._1);
        var groupedContacts = contacts.groupBy(it -> it._1);
        return customers.map(it -> {
            var customerId = it.getEntityId();
            var value = it.getValue();
            var customerSecrets = groupedSecrets.get(customerId)
                .getOrElse(List.empty())
                .map(v -> v._2)
                .toJavaArray(CustomerSecret[]::new);
            var customerSecretsEx = groupedSecretsEx.get(customerId)
                .getOrElse(List.empty())
                .map(v -> v._2)
                .toJavaArray(CustomerSecretEx[]::new);
            var customerContacts = groupedContacts.get(customerId)
                .getOrElse(List.empty())
                .map(v -> v._2())
                .toJavaArray(CustomerContact[]::new);
            return new CustomerModel(
                EntityId.of(it.getProjectId(), it.getEntityId(), it.getVersion()),
                value,
                customerSecrets,
                customerSecretsEx,
                customerContacts);
        }).toList();
    }


    static Tuple2<UUID, CustomerSecret> toCustomerSecret(Row row) {
        var key = row.getUUID("customer_id");
        var result = CustomerSecret.builder()
            .location(row.getString("location"))
            .username(row.getString("username"))
            .password(row.getString("password"))
            .changedWho(Email.of(row.getString("changed_who")))
            .changedWhen(row.getLocalDateTime("changed_when"))
            .build();
        return new Tuple2<>(key, result);
    }

    static Tuple2<UUID, CustomerSecretEx> toCustomerSecretEx(Row row) {
        var key = row.getUUID("customer_id");
        var result = CustomerSecretEx.builder()
            .location(row.getString("location"))
            .username(row.getString("username"))
            .password(row.getString("password"))
            .entityName(row.getString("entity_name"))
            .entityCode(row.getString("entity_code"))
            .changedWho(Email.of(row.getString("changed_who")))
            .changedWhen(row.getLocalDateTime("changed_when"))
            .build();
        return new Tuple2<>(key, result);
    }

    static Tuple2<UUID, CustomerContact> toCustomerContact(Row row) {
        var key = row.getUUID("customer_id");
        var result = CustomerContact.builder()
            .firstName(row.getString("first_name"))
            .lastName(row.getString("last_name"))
            .phoneNo(row.getString("phone_no"))
            .email(row.getString("email"))
            .build();
        return new Tuple2<>(key, result);
    }

    static Entity<CustomerValue> toCustomerEntity(Row row) {
        var value = CustomerValue.builder()
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
        .daneTechniczne(row.getString("dane_techniczne"))
        .build();
        var projectId = row.getUUID("project_id");
        var entityId = row.getUUID("entity_id");
        var entityVersion = row.getInteger("entity_version");
        return value.withId(projectId, entityId, entityVersion);
    }
}
