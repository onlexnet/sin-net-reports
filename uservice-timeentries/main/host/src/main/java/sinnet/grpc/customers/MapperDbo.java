package sinnet.grpc.customers;

import java.util.List;

import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Name;
import sinnet.models.ValEmail;
import sinnet.models.ShardedId;

public interface MapperDbo {

  default CustomerContact fromDbo(CustomerRepository.CustomerDboContact dbo) {
    return new CustomerContact()
      .setFirstName(dbo.getFirstName())
      .setLastName(dbo.getLastName())
      .setPhoneNo(dbo.getPhoneNo())
      .setEmail(dbo.getEmail());
  }

  default CustomerSecretEx fromDbo(CustomerRepository.CustomerDboSecretEx dbo) {
    return new CustomerSecretEx()
      .setUsername(dbo.getUsername())
      .setPassword(dbo.getPassword())
      .setLocation(dbo.getLocation())
      .setEntityName(dbo.getEntityName())
      .setEntityCode(dbo.getEntityCode())
      .setChangedWho(ValEmail.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen());
  }

  default CustomerSecret fromDbo(CustomerRepository.CustomerDboSecret dbo) {
    return new CustomerSecret()
      .setUsername(dbo.getUsername())
      .setPassword(dbo.getPassword())
      .setLocation(dbo.getLocation())
      .setChangedWho(ValEmail.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen());
  }

  default CustomerModel fromDbo(CustomerRepository.CustomerDbo dbo) {
    var id = ShardedId.of(dbo.getProjectId(), dbo.getEntityId() , dbo.getEntityVersion());
    var contacts = dbo.getContacts().stream().map(this::fromDbo).toList();
    var secrets = dbo.getSecrets().stream().map(this::fromDbo).toList();
    var secretsEx = dbo.getSecretsEx().stream().map(this::fromDbo).toList();
    var value = new CustomerValue()
      .operatorEmail(ValEmail.of(dbo.getOperatorEmail()))
      .billingModel(dbo.getBillingModel())
      .supportStatus(dbo.getSupportStatus())
      .distance(dbo.getDistance())
      .customerName(Name.of(dbo.getCustomerName()))
      .customerCityName(Name.of(dbo.getCustomerCityName()))
      .customerAddress(dbo.getCustomerAddress())
      .nfzUmowa(dbo.getNfzUmowa())
      .nfzMaFilie(dbo.getNfzMaFilie())
      .nfzLekarz(dbo.getNfzLekarz())
      .nfzPolozna(dbo.getNfzPolozna())
      .nfzPielegniarkaSrodowiskowa(dbo.getNfzPielegniarka_srodowiskowa())
      .nfzMedycynaSzkolna(dbo.getNfz_medycyna_szkolna())
      .nfzTransportSanitarny(dbo.getNfz_transport_sanitarny())
      .nfzNocnaPomocLekarska(dbo.getNfz_nocna_pomoc_lekarska())
      .nfzAmbulatoryjnaOpiekaSpecjalistyczna(dbo.getNfz_ambulatoryjna_opieka_specjalistyczna())
      .nfzRehabilitacja(dbo.getNfz_rehabilitacja())
      .nfzStomatologia(dbo.getNfz_stomatologia())
      .nfzPsychiatria(dbo.getNfz_psychiatria())
      .nfzSzpitalnictwo(dbo.getNfzSzpitalnictwo())
      .nfzProgramyProfilaktyczne(dbo.getNfzProgramyProfilaktyczne())
      .nfzZaopatrzenieOrtopedyczne(dbo.getNfzZaopatrzenieOrtopedyczne())
      .nfzOpiekaDlugoterminowa(dbo.getNfzOpiekaDlugoterminowa())
      .nfzNotatki(dbo.getNfzNotatki())
      .komercjaJest(dbo.getKomercjaJest())
      .komercjaNotatki(dbo.getKomercjaNotatki())
      .daneTechniczne(dbo.getDaneTechniczne());
    return new CustomerModel().setId(id).setValue(value).setContacts(contacts).setSecrets(secrets).setSecretsEx(secretsEx);
  }

}
