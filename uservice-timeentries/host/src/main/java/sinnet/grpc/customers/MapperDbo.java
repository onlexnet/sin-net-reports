package sinnet.grpc.customers;

import static java.util.Optional.ofNullable;

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
    var id = ShardedId.of(dbo.getProjectId(), dbo.getEntityId(), ofNullable(dbo.getEntityVersion()).orElse(0L));
    var contacts = dbo.getContacts().stream().map(this::fromDbo).toList();
    var secrets = dbo.getSecrets().stream().map(this::fromDbo).toList();
    var secretsEx = dbo.getSecretsEx().stream().map(this::fromDbo).toList();
    var value = new CustomerValue()
        .setOperatorEmail(ValEmail.of(dbo.getOperatorEmail()))
        .setBillingModel(dbo.getBillingModel())
        .setSupportStatus(dbo.getSupportStatus())
        .setDistance(ofNullable(dbo.getDistance()).orElse(0))
        .setCustomerName(ValName.of(dbo.getCustomerName()))
        .setCustomerCityName(ValName.of(dbo.getCustomerCityName()))
        .setCustomerAddress(dbo.getCustomerAddress())
        .setNfzUmowa(ofNullable(dbo.getNfzUmowa()).orElse(false))
        .setNfzMaFilie(ofNullable(dbo.getNfzMaFilie()).orElse(false))
        .setNfzLekarz(ofNullable(dbo.getNfzLekarz()).orElse(false))
        .setNfzPolozna(ofNullable(dbo.getNfzPolozna()).orElse(false))
        .setNfzPielegniarkaSrodowiskowa(ofNullable(dbo.getNfzPielegniarkaSrodowiskowa()).orElse(false))
        .setNfzMedycynaSzkolna(ofNullable(dbo.getNfzMedycynaSzkolna()).orElse(false))
        .setNfzTransportSanitarny(ofNullable(dbo.getNfzTransportSanitarny()).orElse(false))
        .setNfzNocnaPomocLekarska(ofNullable(dbo.getNfzNocnaPomocLekarska()).orElse(false))
        .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(ofNullable(dbo.getNfzAmbulatoryjnaOpiekaSpecjalistyczna()).orElse(false))
        .setNfzRehabilitacja(ofNullable(dbo.getNfzRehabilitacja()).orElse(false))
        .setNfzStomatologia(ofNullable(dbo.getNfzStomatologia()).orElse(false))
        .setNfzPsychiatria(ofNullable(dbo.getNfzPsychiatria()).orElse(false))
        .setNfzSzpitalnictwo(ofNullable(dbo.getNfzSzpitalnictwo()).orElse(false))
        .setNfzProgramyProfilaktyczne(ofNullable(dbo.getNfzProgramyProfilaktyczne()).orElse(false))
        .setNfzZaopatrzenieOrtopedyczne(ofNullable(dbo.getNfzZaopatrzenieOrtopedyczne()).orElse(false))
        .setNfzOpiekaDlugoterminowa(ofNullable(dbo.getNfzOpiekaDlugoterminowa()).orElse(false))
        .setNfzNotatki(dbo.getNfzNotatki())
        .setKomercjaJest(ofNullable(dbo.getKomercjaJest()).orElse(false))
        .setKomercjaNotatki(dbo.getKomercjaNotatki())
        .setDaneTechniczne(dbo.getDaneTechniczne());
    return new CustomerModel().setId(id).setValue(value).setContacts(contacts).setSecrets(secrets).setSecretsEx(secretsEx);
  }

}
