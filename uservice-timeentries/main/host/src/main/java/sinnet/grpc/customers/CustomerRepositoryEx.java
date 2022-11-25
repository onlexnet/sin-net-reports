package sinnet.grpc.customers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.annotation.Tainted;
import javax.annotation.Untainted;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.vavr.Tuple2;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import sinnet.grpc.customers.CustomerRepository.CustomerDbo;
import sinnet.grpc.customers.CustomerRepository.CustomerDboContact;
import sinnet.grpc.customers.CustomerRepository.CustomerDboSecret;
import sinnet.grpc.customers.CustomerRepository.CustomerDboSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ShardedId;

@RequiredArgsConstructor
public final class CustomerRepositoryEx implements MapperDbo {

  private final CustomerRepository repository;

  /**
   * Returns a model pointed by given {@code id} or empty value if the model does
   * not exists.
   *
   * @param id if of the requested model.
   */
  public Optional<CustomerModel> get(ShardedId id) {
    var probe = new CustomerDbo().setProjectId(id.getProjectId()).setId(id.getId()).setVersion(id.getVersion());
    var example = Example.of(probe);
    var maybeResult = repository.findOne(example);
    return maybeResult.map(this::fromDbo);
  }

  // /**
  // * Returns a latest model pointed by given {@code projectId} {@code id} or
  // empty
  // * value if the model does not exists.
  // *
  // * @param customerId if of the requested model.
  // */
  // Optional<CustomerModel> get(UUID projectId, UUID customerId) {
  // }

  /**
   * Saves a new version of Entity identified by given eid.
   *
   * @return new EID for just stored entity.
   */
  ShardedId write(CustomerModel model) {

    var eid = model.getId();
    var value = model.getValue();
    var secrets = model.getSecrets();
    var secretsEx = model.getSecretsEx();
    var contacts = model.getContacts();
    var projectId = eid.getProjectId();
    var entityId = eid.getId();
    var version = eid.getVersion();
    var dbo = repository.findByProjectidEntityid(projectId, entityId);
    dbo.setVersion(version);
    dbo.setCustomerName(value.customerName().getValue())
        .setCustomerCityName(value.customerCityName().getValue())
        .setCustomerAddress(value.customerAddress())
        .setOperatorEmail(value.operatorEmail().getValue())
        .setBillingModel(value.billingModel())
        .setSupportStatus(value.supportStatus())
        .setDistance(value.distance())
        .setNfzUmowa(value.nfzUmowa())
        .setNfzMaFilie(value.nfzMaFilie())
        .setNfz_lekarz(value.nfzLekarz())
        .setNfzPolozna(value.nfzPolozna())
        .setNfz_pielegniarka_srodowiskowa(value.nfzPielegniarkaSrodowiskowa())
        .setNfz_medycyna_szkolna(value.nfzMedycynaSzkolna())
        .setNfz_transport_sanitarny(value.nfzTransportSanitarny())
        .setNfz_nocna_pomoc_lekarska(value.nfzNocnaPomocLekarska())
        .setNfz_ambulatoryjna_opieka_specjalistyczna(value.nfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .setNfz_rehabilitacja(value.nfzRehabilitacja())
        .setNfz_stomatologia(value.nfzStomatologia())
        .setNfz_psychiatria(value.nfzPsychiatria())
        .setNfzSzpitalnictwo(value.nfzSzpitalnictwo())
        .setNfzProgramyProfilaktyczne(value.nfzProgramyProfilaktyczne())
        .setNfzZaopatrzenieOrtopedyczne(value.nfzZaopatrzenieOrtopedyczne())
        .setNfzOpiekaDlugoterminowa(value.nfzOpiekaDlugoterminowa())
        .setNfzNotatki(value.nfzNotatki())
        .setKomercjaJest(value.komercjaJest())
        .setKomercjaNotatki(value.komercjaNotatki())
        .setDaneTechniczne(value.daneTechniczne())
        .setContacts(contacts.stream().map(this::toDbo).toList())
        .setSecrets(secrets.stream().map(this::toDbo).toList())
        .setSecretsEx(secretsEx.stream().map(this::toDbo).toList());
    repository.save(dbo);
    return eid.next();
  }

  CustomerDboContact toDbo(CustomerContact it) {
    return new CustomerDboContact()
        .setFirstName(it.getFirstName())
        .setLastName(it.getLastName())
        .setPhoneNo(it.getPhoneNo())
        .setEmail(it.getEmail());
  }

  CustomerDboSecret toDbo(CustomerSecret it) {
    return new CustomerDboSecret()
        .setLocation(it.getLocation())
        .setPassword(it.getPassword())
        .setUsername(it.getUsername())
        .setChangedWhen(it.getChangedWhen())
        .setChangedWho(it.getChangedWho().getValue());
  }

  CustomerDboSecretEx toDbo(CustomerSecretEx it) {
    return new CustomerDboSecretEx()
        .setLocation(it.getLocation())
        .setPassword(it.getPassword())
        .setUsername(it.getUsername())
        .setEntityCode(it.getEntityCode())
        .setEntityName(it.getEntityName())
        .setChangedWhen(it.getChangedWhen())
        .setChangedWho(it.getChangedWho().getValue());
  }

  public Boolean remove(ShardedId id) {
    var projectId = id.getProjectId();
    var eid = id.getId();
    var etag = id.getVersion();
    repository.deleteByProjectidEntityidEntityversion(projectId, eid, etag);
    return true;
  }

}
