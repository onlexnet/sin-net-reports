package sinnet.grpc.customers;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.grpc.customers.CustomerRepository.CustomerDbo;
import sinnet.grpc.customers.CustomerRepository.CustomerDboContact;
import sinnet.grpc.customers.CustomerRepository.CustomerDboSecret;
import sinnet.grpc.customers.CustomerRepository.CustomerDboSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.EntityVersion;
import sinnet.models.ShardedId;

/**
 * TBD.
 */
@Component
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
    var probe = new CustomerDbo().setProjectId(id.projectId()).setEntityId(id.id()).setEntityVersion(EntityVersion.toDbo(id.version()));
    var example = Example.of(probe);
    var maybeResult = repository.findOne(example);
    return maybeResult.map(this::fromDbo);
  }

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
    var projectId = eid.projectId();
    var entityId = eid.id();
    var version = eid.version();
    var dbo = repository.findByProjectIdAndEntityId(projectId, entityId);
    if (dbo == null) {
      dbo = new CustomerDbo();
      dbo.setProjectId(projectId);
      dbo.setEntityId(entityId);
      dbo.setEntityVersion(EntityVersion.toDbo(version));
    }
    dbo.setCustomerName(value.getCustomerName().getValue())
        .setCustomerCityName(value.getCustomerCityName().getValue())
        .setCustomerAddress(value.getCustomerAddress())
        .setOperatorEmail(value.getOperatorEmail().value())
        .setBillingModel(value.getBillingModel())
        .setSupportStatus(value.getSupportStatus())
        .setDistance(value.getDistance())
        .setNfzUmowa(value.isNfzUmowa())
        .setNfzMaFilie(value.isNfzMaFilie())
        .setNfzLekarz(value.isNfzLekarz())
        .setNfzPolozna(value.isNfzPolozna())
        .setNfzPielegniarkaSrodowiskowa(value.isNfzPielegniarkaSrodowiskowa())
        .setNfzMedycynaSzkolna(value.isNfzMedycynaSzkolna())
        .setNfzTransportSanitarny(value.isNfzTransportSanitarny())
        .setNfzNocnaPomocLekarska(value.isNfzNocnaPomocLekarska())
        .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(value.isNfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .setNfzRehabilitacja(value.isNfzRehabilitacja())
        .setNfzStomatologia(value.isNfzStomatologia())
        .setNfzPsychiatria(value.isNfzPsychiatria())
        .setNfzSzpitalnictwo(value.isNfzSzpitalnictwo())
        .setNfzProgramyProfilaktyczne(value.isNfzProgramyProfilaktyczne())
        .setNfzZaopatrzenieOrtopedyczne(value.isNfzZaopatrzenieOrtopedyczne())
        .setNfzOpiekaDlugoterminowa(value.isNfzOpiekaDlugoterminowa())
        .setNfzNotatki(value.getNfzNotatki())
        .setKomercjaJest(value.isKomercjaJest())
        .setKomercjaNotatki(value.getKomercjaNotatki())
        .setDaneTechniczne(value.getDaneTechniczne())
        .setContacts(contacts.stream().map(this::toDbo).collect(Collectors.toSet()))
        .setSecrets(secrets.stream().map(this::toDbo).collect(Collectors.toSet()))
        .setSecretsEx(secretsEx.stream().map(this::toDbo).collect(Collectors.toSet()));
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
        .setChangedWho(it.getChangedWho().value())
        .setOtpSecret(it.getOtpSecret())
        .setOtpRecoveryKeys(it.getOtpRecoveryKeys());
  }

  CustomerDboSecretEx toDbo(CustomerSecretEx it) {
    return new CustomerDboSecretEx()
        .setLocation(it.getLocation())
        .setPassword(it.getPassword())
        .setUsername(it.getUsername())
        .setEntityCode(it.getEntityCode())
        .setEntityName(it.getEntityName())
        .setChangedWhen(it.getChangedWhen())
        .setChangedWho(it.getChangedWho().value())
        .setOtpSecret(it.getOtpSecret())
        .setOtpRecoveryKeys(it.getOtpRecoveryKeys());
  }



  /**
   * TBD.
   */
  public Boolean remove(ShardedId id) {
    var projectId = id.projectId();
    var eid = id.id();
    var etag = EntityVersion.toDbo(id.version());
    repository.deleteByProjectIdAndEntityIdAndEntityVersion(projectId, eid, etag);
    return true;
  }

}
