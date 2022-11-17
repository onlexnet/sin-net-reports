package sinnet.gql.customers;

import java.util.List;

import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Name;
import sinnet.models.Email;
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
      .setChangedWho(Email.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen());
  }

  default CustomerSecret fromDbo(CustomerRepository.CustomerDboSecret dbo) {
    return new CustomerSecret()
      .setUsername(dbo.getUsername())
      .setPassword(dbo.getPassword())
      .setLocation(dbo.getLocation())
      .setChangedWho(Email.of(dbo.getChangedWho()))
      .setChangedWhen(dbo.getChangedWhen());
  }

  default CustomerModel fromDbo(CustomerRepository.CustomerDbo dbo) {
    var id = ShardedId.of(dbo.getProjectId(), dbo.getId() , dbo.getVersion());
    var contacts = dbo.getContacts().stream().map(this::fromDbo).toList();
    var secrets = dbo.getSecrets().stream().map(this::fromDbo).toList();
    var secretsEx = dbo.getSecretsEx().stream().map(this::fromDbo).toList();
    var value = new CustomerValue()
      .setOperatorEmail(dbo.getOperatorEmail())
      .setBillingModel(dbo.getBillingModel())
      .setSupportStatus(dbo.getSupportStatus())
      .setDistance(dbo.getDistance())
      .setCustomerName(Name.of(dbo.getCustomerName()))
      .setCustomerCityName(Name.of(dbo.getCustomerCityName()))
      .setCustomerAddress(dbo.getCustomerAddress())
      .setNfzUmowa(dbo.getNfzUmowa())
      .setNfzMaFilie(dbo.getNfzMaFilie())
      .setNfzLekarz(dbo.getNfz_lekarz())
      .setNfzPolozna(dbo.getNfzPolozna())
      .setNfzPielegniarkaSrodowiskowa(dbo.getNfz_pielegniarka_srodowiskowa())
      .setNfzMedycynaSzkolna(dbo.getNfz_medycyna_szkolna())
      .setNfzTransportSanitarny(dbo.getNfz_transport_sanitarny())
      .setNfzNocnaPomocLekarska(dbo.getNfz_nocna_pomoc_lekarska())
      .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(dbo.getNfz_ambulatoryjna_opieka_specjalistyczna())
      .setNfzRehabilitacja(dbo.getNfz_rehabilitacja())
      .setNfzStomatologia(dbo.getNfz_stomatologia())
      .setNfzPsychiatria(dbo.getNfz_psychiatria())
      .setNfzSzpitalnictwo(dbo.getNfz_szpitalnictwo())
      .setNfzProgramyProfilaktyczne(dbo.getNfz_programy_profilaktyczne())
      .setNfzZaopatrzenieOrtopedyczne(dbo.getNfz_zaopatrzenie_ortopedyczne())
      .setNfzOpiekaDlugoterminowa(dbo.getNfz_opieka_dlugoterminowa())
      .setNfzNotatki(dbo.getNfz_notatki())
      .setKomercjaJest(dbo.getKomercja_jest())
      .setKomercjaNotatki(dbo.getNfz_notatki())
      .setDaneTechniczne(dbo.getDane_techniczne());
    return new CustomerModel().setId(id).setValue(value).setContacts(contacts).setSecrets(secrets).setSecretsEx(secretsEx);
  }

}
