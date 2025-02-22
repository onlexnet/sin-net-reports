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
        .operatorEmail(ValEmail.of(dbo.getOperatorEmail()))
        .billingModel(dbo.getBillingModel())
        .supportStatus(dbo.getSupportStatus())
        .distance(ofNullable(dbo.getDistance()).orElse(0))
        .customerName(ValName.of(dbo.getCustomerName()))
        .customerCityName(ValName.of(dbo.getCustomerCityName()))
        .customerAddress(dbo.getCustomerAddress())
        .nfzUmowa(ofNullable(dbo.getNfzUmowa()).orElse(false))
        .nfzMaFilie(ofNullable(dbo.getNfzMaFilie()).orElse(false))
        .nfzLekarz(ofNullable(dbo.getNfzLekarz()).orElse(false))
        .nfzPolozna(ofNullable(dbo.getNfzPolozna()).orElse(false))
        .nfzPielegniarkaSrodowiskowa(ofNullable(dbo.getNfzPielegniarkaSrodowiskowa()).orElse(false))
        .nfzMedycynaSzkolna(ofNullable(dbo.getNfzMedycynaSzkolna()).orElse(false))
        .nfzTransportSanitarny(ofNullable(dbo.getNfzTransportSanitarny()).orElse(false))
        .nfzNocnaPomocLekarska(ofNullable(dbo.getNfzNocnaPomocLekarska()).orElse(false))
        .nfzAmbulatoryjnaOpiekaSpecjalistyczna(ofNullable(dbo.getNfzAmbulatoryjnaOpiekaSpecjalistyczna()).orElse(false))
        .nfzRehabilitacja(ofNullable(dbo.getNfzRehabilitacja()).orElse(false))
        .nfzStomatologia(ofNullable(dbo.getNfzStomatologia()).orElse(false))
        .nfzPsychiatria(ofNullable(dbo.getNfzPsychiatria()).orElse(false))
        .nfzSzpitalnictwo(ofNullable(dbo.getNfzSzpitalnictwo()).orElse(false))
        .nfzProgramyProfilaktyczne(ofNullable(dbo.getNfzProgramyProfilaktyczne()).orElse(false))
        .nfzZaopatrzenieOrtopedyczne(ofNullable(dbo.getNfzZaopatrzenieOrtopedyczne()).orElse(false))
        .nfzOpiekaDlugoterminowa(ofNullable(dbo.getNfzOpiekaDlugoterminowa()).orElse(false))
        .nfzNotatki(dbo.getNfzNotatki())
        .komercjaJest(ofNullable(dbo.getKomercjaJest()).orElse(false))
        .komercjaNotatki(dbo.getKomercjaNotatki())
        .daneTechniczne(dbo.getDaneTechniczne());
    return new CustomerModel().setId(id).setValue(value).setContacts(contacts).setSecrets(secrets).setSecretsEx(secretsEx);
  }

}
