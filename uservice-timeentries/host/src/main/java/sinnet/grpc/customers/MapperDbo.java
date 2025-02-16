package sinnet.grpc.customers;

import sinnet.domain.model.ValEmail;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ShardedId;
import sinnet.models.ValName;

/**
 * TBD.
 */
public interface MapperDbo {

  /**
   * TBD.
   */
  default CustomerContact fromDbo(CustomerRepository.CustomerDboContact dbo) {
    return new CustomerContact()
      .setFirstName(dbo.getFirstName())
      .setLastName(dbo.getLastName())
      .setPhoneNo(dbo.getPhoneNo())
      .setEmail(dbo.getEmail());
  }

  /**
   * TBD.
   */
  default CustomerSecretEx fromDbo(CustomerRepository.CustomerDboSecretEx dbo) {
    return new CustomerSecretEx()
      .setUsername(dbo.getUsername())
      .setPassword(dbo.getPassword())
      .setLocation(dbo.getLocation())
      .setEntityName(dbo.getEntityName())
      .setEntityCode(dbo.getEntityCode())
      .setChangedWho(ValEmail.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen())
      .setOtpSecret(dbo.getOtpSecret())
      .setOtpRecoveryKeys(dbo.getOtpRecoveryKeys());
  }

  /**
   * TBD.
   */
  default CustomerSecret fromDbo(CustomerRepository.CustomerDboSecret dbo) {
    return new CustomerSecret()
      .setUsername(dbo.getUsername())
      .setPassword(dbo.getPassword())
      .setLocation(dbo.getLocation())
      .setChangedWho(ValEmail.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen())
      .setOtpSecret(dbo.getOtpSecret())
      .setOtpRecoveryKeys(dbo.getOtpRecoveryKeys());
  }

  /**
   * TBD.
   */
  default CustomerModel fromDbo(CustomerRepository.CustomerDbo dbo) {
    var id = ShardedId.of(dbo.getProjectId(), dbo.getEntityId(), dbo.getEntityVersion());
    var contacts = dbo.getContacts().stream().map(this::fromDbo).toList();
    var secrets = dbo.getSecrets().stream().map(this::fromDbo).toList();
    var secretsEx = dbo.getSecretsEx().stream().map(this::fromDbo).toList();
    var value = new CustomerValue()
        .setOperatorEmail(ValEmail.of(dbo.getOperatorEmail()))
        .setBillingModel(dbo.getBillingModel())
        .setSupportStatus(dbo.getSupportStatus())
        .setDistance(dbo.getDistance())
        .setCustomerName(ValName.of(dbo.getCustomerName()))
        .setCustomerCityName(ValName.of(dbo.getCustomerCityName()))
        .setCustomerAddress(dbo.getCustomerAddress())
        .setNfzUmowa(dbo.getNfzUmowa())
        .setNfzMaFilie(dbo.getNfzMaFilie())
        .setNfzLekarz(dbo.getNfzLekarz())
        .setNfzPolozna(dbo.getNfzPolozna())
        .setNfzPielegniarkaSrodowiskowa(dbo.getNfzPielegniarkaSrodowiskowa())
        .setNfzMedycynaSzkolna(dbo.getNfzMedycynaSzkolna())
        .setNfzTransportSanitarny(dbo.getNfzTransportSanitarny())
        .setNfzNocnaPomocLekarska(dbo.getNfzNocnaPomocLekarska())
        .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(dbo.getNfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .setNfzRehabilitacja(dbo.getNfzRehabilitacja())
        .setNfzStomatologia(dbo.getNfzStomatologia())
        .setNfzPsychiatria(dbo.getNfzPsychiatria())
        .setNfzSzpitalnictwo(dbo.getNfzSzpitalnictwo())
        .setNfzProgramyProfilaktyczne(dbo.getNfzProgramyProfilaktyczne())
        .setNfzZaopatrzenieOrtopedyczne(dbo.getNfzZaopatrzenieOrtopedyczne())
        .setNfzOpiekaDlugoterminowa(dbo.getNfzOpiekaDlugoterminowa())
        .setNfzNotatki(dbo.getNfzNotatki())
        .setKomercjaJest(dbo.getKomercjaJest())
        .setKomercjaNotatki(dbo.getKomercjaNotatki())
        .setDaneTechniczne(dbo.getDaneTechniczne());
    return new CustomerModel().setId(id).setValue(value).setContacts(contacts).setSecrets(secrets).setSecretsEx(secretsEx);
  }

}
