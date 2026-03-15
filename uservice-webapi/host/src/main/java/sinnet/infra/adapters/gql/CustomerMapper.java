package sinnet.infra.adapters.gql;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import io.vavr.collection.Iterator;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.domain.models.Customer;
import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerValue;
import sinnet.gql.models.CustomerContactGql;
import sinnet.gql.models.CustomerContactInputGql;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerModelGql;
import sinnet.gql.models.CustomerSecretExGql;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretGql;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.EntityGql;
import sinnet.gql.models.SomeEntityGql;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface CustomerMapper {

  CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

  @Mapping(target = "id", source = "entityId")
  @Mapping(target = "tag", source = "entityVersion")
  sinnet.domain.models.EntityId map(EntityGql item);

  @Mapping(target = "nfzUmowa", source = "nfzUmowa", defaultValue = "false")
  @Mapping(target = "nfzMaFilie", source = "nfzMaFilie", defaultValue = "false")
  @Mapping(target = "nfzLekarz", source = "nfzLekarz", defaultValue = "false")
  @Mapping(target = "nfzPolozna", source = "nfzPolozna", defaultValue = "false")
  @Mapping(target = "nfzPielegniarkaSrodowiskowa", source = "nfzPielegniarkaSrodowiskowa", defaultValue = "false")
  @Mapping(target = "nfzMedycynaSzkolna", source = "nfzMedycynaSzkolna", defaultValue = "false")
  @Mapping(target = "nfzTransportSanitarny", source = "nfzTransportSanitarny", defaultValue = "false")
  @Mapping(target = "nfzNocnaPomocLekarska", source = "nfzNocnaPomocLekarska", defaultValue = "false")
  @Mapping(target = "nfzAmbulatoryjnaOpiekaSpecjalistyczna", source = "nfzAmbulatoryjnaOpiekaSpecjalistyczna", defaultValue = "false")
  @Mapping(target = "nfzRehabilitacja", source = "nfzRehabilitacja", defaultValue = "false")
  @Mapping(target = "nfzStomatologia", source = "nfzStomatologia", defaultValue = "false")
  @Mapping(target = "nfzPsychiatria", source = "nfzPsychiatria", defaultValue = "false")
  @Mapping(target = "nfzSzpitalnictwo", source = "nfzSzpitalnictwo", defaultValue = "false")
  @Mapping(target = "nfzProgramyProfilaktyczne", source = "nfzProgramyProfilaktyczne", defaultValue = "false")
  @Mapping(target = "nfzZaopatrzenieOrtopedyczne", source = "nfzZaopatrzenieOrtopedyczne", defaultValue = "false")
  @Mapping(target = "nfzOpiekaDlugoterminowa", source = "nfzOpiekaDlugoterminowa", defaultValue = "false")
  @Mapping(target = "komercjaJest", source = "komercjaJest", defaultValue = "false")
  @Mapping(target = "distance", source = "distance", defaultValue = "0")
  CustomerEntry toDomain(CustomerInput item);

  CustomerSecret toDomain(CustomerSecretInput item);

  sinnet.domain.models.CustomerSecretEx toDomain(CustomerSecretExInput item);

  CustomerContact toDomain(CustomerContactInputGql item);

  default SomeEntityGql toGql(sinnet.domain.models.EntityId id) {
    if (id == null) {
      return null;
    }
    return new SomeEntityGql()
        .setEntityId(id.id().toString())
        .setEntityVersion(id.tag())
        .setProjectId(id.projectId().toString());
  }

  default CustomerContactGql toGql(CustomerContact it) {
    if (it == null) {
      return null;
    }
    var result = new CustomerContactGql();
    result.setFirstName(it.firstName());
    result.setLastName(it.lastName());
    result.setPhoneNo(it.phoneNo());
    result.setEmail(it.email());
    return result;
  }

  default CustomerSecretExGql toGql(sinnet.domain.models.CustomerSecretEx it) {
    if (it == null) {
      return null;
    }
    var result = new CustomerSecretExGql();
    result.setLocation(it.location());
    result.setUsername(it.username());
    result.setPassword(it.password());
    result.setEntityCode(it.entityCode());
    result.setEntityName(it.entityName());
    result.setChangedWhen("?");
    result.setChangedWho("?");
    result.setOtpSecret(it.otpSecret());
    result.setOtpRecoveryKeys(it.otpRecoveryKeys());
    return result;
  }

  default CustomerSecretGql toGql(CustomerSecret it) {
    if (it == null) {
      return null;
    }
    var result = new CustomerSecretGql();
    result.setLocation(it.location());
    result.setUsername(it.username());
    result.setPassword(it.password());
    result.setChangedWhen("?");
    result.setChangedWho("?");
    result.setOtpSecret(it.otpSecret());
    result.setOtpRecoveryKeys(it.otpRecoveryKeys());
    return result;
  }

  default CustomerEntityGql toGql(Customer item) {
    if (item == null) {
      return null;
    }
    var result = new CustomerEntityGql();
    result.setId(toGql(item.id()));
    result.setData(toGql(item.value()));
    result.setSecrets(Iterator.ofAll(item.value().secrets()).map(this::toGql).toJavaArray(CustomerSecretGql[]::new));
    result.setSecretsEx(Iterator.ofAll(item.value().secretsEx()).map(this::toGql).toJavaArray(CustomerSecretExGql[]::new));
    result.setContacts(Iterator.ofAll(item.value().contacts()).map(this::toGql).toJavaArray(CustomerContactGql[]::new));
    return result;
  }

  default CustomerEntityGql toGql(CustomerGetResult item) {
    if (item == null) {
      return null;
    }
    var result = new CustomerEntityGql();
    result.setId(toGql(item.id()));
    result.setData(toGql(item.value().entry()));
    result.setSecrets(Iterator.ofAll(item.value().secrets()).map(this::toGql).toJavaArray(CustomerSecretGql[]::new));
    result.setSecretsEx(Iterator.ofAll(item.value().secretsEx()).map(this::toGql).toJavaArray(CustomerSecretExGql[]::new));
    result.setContacts(Iterator.ofAll(item.value().contacts()).map(this::toGql).toJavaArray(CustomerContactGql[]::new));
    return result;
  }

  default CustomerModelGql toGql(CustomerValue item) {
    if (item == null) {
      return null;
    }
    return toGql(item.entry());
  }

  default CustomerModelGql toGql(CustomerEntry entry) {
    if (entry == null) {
      return null;
    }
    var it = new CustomerModelGql();
    it.setOperatorEmail(entry.operatorEmail());
    it.setBillingModel(entry.billingModel());
    it.setSupportStatus(entry.supportStatus());
    it.setDistance(entry.distance());
    it.setCustomerName(entry.customerName());
    it.setCustomerCityName(entry.customerCityName());
    it.setCustomerAddress(entry.customerAddress());
    it.setNfzUmowa(entry.nfzUmowa());
    it.setNfzMaFilie(entry.nfzMaFilie());
    it.setNfzLekarz(entry.nfzLekarz());
    it.setNfzPolozna(entry.nfzPolozna());
    it.setNfzPielegniarkaSrodowiskowa(entry.nfzPielegniarkaSrodowiskowa());
    it.setNfzMedycynaSzkolna(entry.nfzMedycynaSzkolna());
    it.setNfzTransportSanitarny(entry.nfzTransportSanitarny());
    it.setNfzNocnaPomocLekarska(entry.nfzNocnaPomocLekarska());
    it.setNfzAmbulatoryjnaOpiekaSpecjalistyczna(entry.nfzAmbulatoryjnaOpiekaSpecjalistyczna());
    it.setNfzRehabilitacja(entry.nfzRehabilitacja());
    it.setNfzStomatologia(entry.nfzStomatologia());
    it.setNfzPsychiatria(entry.nfzPsychiatria());
    it.setNfzSzpitalnictwo(entry.nfzSzpitalnictwo());
    it.setNfzProgramyProfilaktyczne(entry.nfzProgramyProfilaktyczne());
    it.setNfzZaopatrzenieOrtopedyczne(entry.nfzZaopatrzenieOrtopedyczne());
    it.setNfzOpiekaDlugoterminowa(entry.nfzOpiekaDlugoterminowa());
    it.setNfzNotatki(entry.nfzNotatki());
    it.setKomercjaJest(entry.komercjaJest());
    it.setKomercjaNotatki(entry.komercjaNotatki());
    it.setDaneTechniczne(entry.daneTechniczne());
    return it;
  }
}
