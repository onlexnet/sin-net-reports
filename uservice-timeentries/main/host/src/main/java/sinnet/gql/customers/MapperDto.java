package sinnet.gql.customers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import sinnet.bus.query.FindCustomer;
import sinnet.gql.common.Mapper;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.common.EntityId;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.Name;

/**
 * Doman <-> Dto translations
 */
public interface MapperDto extends Mapper {

  default LocalDateTime fromDto(sinnet.grpc.customers.LocalDateTime item) {
    return Optional.ofNullable(item)
        .map(it -> LocalDateTime.of(it.getYear(), it.getMonth(),it.getDay(),it.getHour(),it.getMinute(),it.getSecond()))
        .orElse(null);
  }

  default sinnet.grpc.customers.CustomerModel toDto(UUID projectId, FindCustomer.Reply reply) {
    return sinnet.grpc.customers.CustomerModel.newBuilder()
        .setId(PropsBuilder.build(EntityId.newBuilder())
            .set(projectId.toString(), b -> b::setProjectId)
            .set(reply.getEntityId().toString(), b -> b::setEntityId)
            .set(reply.getEntityVersion(), b -> b::setEntityVersion)
            .done().build())
        .setValue(toDto(reply.getValue()))
        .addAllSecrets(map(reply.getSecrets()))
        .addAllSecretEx(map(reply.getSecretsEx()))
        .addAllContacts(map(reply.getContacts()))
        .build();
  }

  default sinnet.grpc.customers.CustomerModel toDto(CustomerModel it) {
    return sinnet.grpc.customers.CustomerModel.newBuilder()
        .setId(PropsBuilder.build(EntityId.newBuilder())
            .set(it.getProjectId().toString(), b -> b::setProjectId)
            .set(it.getEntityId().toString(), b -> b::setEntityId)
            .set(it.getEntityVersion(), b -> b::setEntityVersion)
            .done().build())
        .setValue(toDto(it.getValue()))
        .addAllSecrets(fromDto(it.getSecrets()))
        .addAllSecretEx(fromDto(it.getSecretsEx()))
        .addAllContacts(fromDto(it.getContacts()))
        .build();
  }

  default CustomerContact fromDto(sinnet.grpc.customers.CustomerContact item) {
    return new CustomerContact()
        .setFirstName(item.getFirstName())
        .setLastName(item.getLastName())
        .setPhoneNo(item.getPhoneNo())
        .setEmail(item.getEmail());
  }

  default CustomerSecret fromDto(sinnet.grpc.customers.CustomerSecret item) {
    return new CustomerSecret()
        .setLocation(item.getLocation())
        .setPassword(item.getPassword())
        .setUsername(item.getUsername())
        .setChangedWho(Email.of(item.getChangedWho()))
        .setChangedWhen(fromDto(item.getChangedWhen()));
  }

  private CustomerModel fromDto(sinnet.grpc.customers.CustomerModel item) {
    return new CustomerModel()
        .setId(fromDto(item.getId()))
        .setValue(fromDto(item.getValue()))
        .setContacts(fromDto(item.getContactsList()))
        .setSecrets(fromDto(item.getSecretsList()))
        .setSecretsEx(fromDto(item.getSecretsExList()));
  }

  default CustomerValue fromDto(sinnet.grpc.customers.CustomerValue dto) {
    if (dto == null)
      return null;
    return new CustomerValue()
        .operatorEmail(dto.getOperatorEmail())
        .supportStatus(dto.getSupportStatus())
        .billingModel(dto.getBillingModel())
        .distance(dto.getDistance())
        .customerName(Name.of(dto.getCustomerName()))
        .customerCityName(Name.of(dto.getCustomerCityName()))
        .customerAddress(dto.getCustomerAddress())
        .nfzUmowa(dto.getNfzUmowa())
        .nfzMaFilie(dto.getNfzMaFilie())
        .nfzLekarz(dto.getNfzLekarz())
        .nfzPolozna(dto.getNfzPolozna())
        .nfzPielegniarkaSrodowiskowa(dto.getNfzPielegniarkaSrodowiskowa())
        .nfzMedycynaSzkolna(dto.getNfzMedycynaSzkolna())
        .nfzTransportSanitarny(dto.getNfzTransportSanitarny())
        .nfzNocnaPomocLekarska(dto.getNfzNocnaPomocLekarska())
        .nfzAmbulatoryjnaOpiekaSpecjalistyczna(dto.getNfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .nfzRehabilitacja(dto.getNfzRehabilitacja())
        .nfzStomatologia(dto.getNfzStomatologia())
        .nfzPsychiatria(dto.getNfzPsychiatria())
        .nfzSzpitalnictwo(dto.getNfzSzpitalnictwo())
        .nfzProgramyProfilaktyczne(dto.getNfzProgramyProfilaktyczne())
        .nfzZaopatrzenieOrtopedyczne(dto.getNfzZaopatrzenieOrtopedyczne())
        .nfzOpiekaDlugoterminowa(dto.getNfzOpiekaDlugoterminowa())
        .nfzNotatki(dto.getNfzNotatki())
        .komercjaJest(dto.getKomercjaJest())
        .komercjaNotatki(dto.getKomercjaNotatki())
        .daneTechniczne(dto.getDaneTechniczne());
  }

  default sinnet.grpc.customers.CustomerValue toDto(CustomerValue value) {
    return PropsBuilder.build(sinnet.grpc.customers.CustomerValue.newBuilder())
        .set(value.getOperatorEmail(), b -> b::setOperatorEmail)
        .set(value.getBillingModel(), b -> b::setBillingModel)
        .set(value.getSupportStatus(), b -> b::setSupportStatus)
        .set(value.getDistance(), b -> b::setDistance)
        .set(value.getCustomerName().getValue(), b -> b::setCustomerName)
        .set(value.getCustomerCityName().getValue(), b -> b::setCustomerCityName)
        .set(value.getCustomerAddress(), b -> b::setCustomerAddress)
        .set(value.isNfzUmowa(), b -> b::setNfzUmowa)
        .set(value.isNfzMaFilie(), b -> b::setNfzMaFilie)
        .set(value.isNfzLekarz(), b -> b::setNfzLekarz)
        .set(value.isNfzPolozna(), b -> b::setNfzPolozna)
        .set(value.isNfzPielegniarkaSrodowiskowa(), b -> b::setNfzPielegniarkaSrodowiskowa)
        .set(value.isNfzMedycynaSzkolna(), b -> b::setNfzMedycynaSzkolna)
        .set(value.isNfzTransportSanitarny(), b -> b::setNfzTransportSanitarny)
        .set(value.isNfzNocnaPomocLekarska(), b -> b::setNfzNocnaPomocLekarska)
        .set(value.isNfzAmbulatoryjnaOpiekaSpecjalistyczna(), b -> b::setNfzAmbulatoryjnaOpiekaSpecjalistyczna)
        .set(value.isNfzRehabilitacja(), b -> b::setNfzRehabilitacja)
        .set(value.isNfzStomatologia(), b -> b::setNfzStomatologia)
        .set(value.isNfzPsychiatria(), b -> b::setNfzPsychiatria)
        .set(value.isNfzSzpitalnictwo(), b -> b::setNfzSzpitalnictwo)
        .set(value.isNfzProgramyProfilaktyczne(), b -> b::setNfzProgramyProfilaktyczne)
        .set(value.isNfzZaopatrzenieOrtopedyczne(), b -> b::setNfzZaopatrzenieOrtopedyczne)
        .set(value.isNfzOpiekaDlugoterminowa(), b -> b::setNfzOpiekaDlugoterminowa)
        .set(value.getNfzNotatki(), b -> b::setNfzNotatki)
        .set(value.isKomercjaJest(), b -> b::setKomercjaJest)
        .set(value.getKomercjaNotatki(), b -> b::setKomercjaNotatki)
        .set(value.getDaneTechniczne(), b -> b::setDaneTechniczne)
        .done().build();
  }

  private static Iterable<CustomerSecretEx> map(sinnet.models.CustomerSecretEx[] value) {
    return Array.of(value).map(MapperDto::map);
  }

  private static CustomerContact map(sinnet.models.CustomerContact it) {
    return PropsBuilder.build(CustomerContact.newBuilder())
        .set(it.getFirstName(), b -> b::setFirstName)
        .set(it.getLastName(), b -> b::setLastName)
        .set(it.getPhoneNo(), b -> b::setPhoneNo)
        .set(it.getEmail(), b -> b::setEmail)
        .done().build();
  }

  private static Iterable<CustomerContact> map(sinnet.models.CustomerContact[] value) {
    return Array.of(value).map(MapperDto::map);
  }

  private static LocalDateTime map(java.time.LocalDateTime dateTime) {
    if (dateTime == null)
      return null;
    return LocalDateTime.newBuilder()
        .setYear(dateTime.getYear())
        .setMonth(dateTime.getMonthValue())
        .setDay(dateTime.getDayOfMonth())
        .setHour(dateTime.getHour())
        .setMinute(dateTime.getMinute())
        .setSecond(dateTime.getSecond())
        .build();
  }

  private static CustomerSecret map(sinnet.models.CustomerSecret value) {
    return PropsBuilder.build(CustomerSecret.newBuilder())
        .set(Option.of(value.getLocation()).getOrElse("?"), b -> b::setLocation)
        .set(value.getUsername(), b -> b::setUsername)
        .set(value.getPassword(), b -> b::setPassword)
        .set(Option.of(value.getChangedWho().getValue()).getOrElse("?"), b -> b::setChangedWho)
        .set(map(value.getChangedWhen()), b -> b::setChangedWhen)
        .done().build();
  }

  private static Iterable<CustomerSecret> map(sinnet.models.CustomerSecret[] value) {
    return Array.of(value).map(MapperDto::map);
  }

  private static CustomerSecretEx map(sinnet.models.CustomerSecretEx it) {
    return PropsBuilder.build(CustomerSecretEx.newBuilder())
        .set(Option.of(it.getLocation()).getOrElse("?"), b -> b::setLocation)
        .set(it.getUsername(), b -> b::setUsername)
        .set(it.getPassword(), b -> b::setPassword)
        .set(it.getEntityName(), b -> b::setEntityName)
        .set(it.getEntityCode(), b -> b::setEntityCode)
        .set(Option.of(it.getChangedWho().getValue()).getOrElse("?"), b -> b::setChangedWho)
        .set(map(it.getChangedWhen()), b -> b::setChangedWhen)
        .done().build();
  }

}
