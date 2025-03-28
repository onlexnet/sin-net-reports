package sinnet.grpc.customers;

import java.time.LocalDateTime;
import java.util.Optional;

import io.vavr.control.Option;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.Mapper;
import sinnet.grpc.mapping.PropsBuilder;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerModel;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerValue;
import sinnet.models.ValName;

/**
 * Doman <-> Dto translations.
 */
public interface MapperDto extends Mapper {

  /**
   * TBD.
   */
  static sinnet.grpc.customers.CustomerModel toDto(CustomerModel it) {
    return sinnet.grpc.customers.CustomerModel.newBuilder()
        .setId(PropsBuilder.build(sinnet.grpc.common.EntityId.newBuilder())
            .set(it.getId().getProjectId().toString(), b -> b::setProjectId)
            .set(it.getId().getId().toString(), b -> b::setEntityId)
            .set(it.getId().getVersion(), b -> b::setEntityVersion)
            .done().build())
        .setValue(toDto(it.getValue()))
        .addAllSecrets(it.getSecrets().stream().map(MapperDto::toDto).toList())
        .addAllSecretEx(it.getSecretsEx().stream().map(MapperDto::toDto).toList())
        .addAllContacts(it.getContacts().stream().map(MapperDto::toDto).toList())
        .build();
  }

  /**
   * TBD.
   */
  static sinnet.grpc.customers.CustomerValue toDto(CustomerValue value) {
    return PropsBuilder.build(sinnet.grpc.customers.CustomerValue.newBuilder())
        .set(value.getOperatorEmail(), ValEmail::value, b -> b::setOperatorEmail)
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

  /**
   * TBD.
   */
  static sinnet.grpc.customers.CustomerContact toDto(CustomerContact it) {
    return PropsBuilder.build(sinnet.grpc.customers.CustomerContact.newBuilder())
        .set(it.getFirstName(), b -> b::setFirstName)
        .set(it.getLastName(), b -> b::setLastName)
        .set(it.getPhoneNo(), b -> b::setPhoneNo)
        .set(it.getEmail(), b -> b::setEmail)
        .done().build();
  }

  /**
   * TBD.
   */
  static sinnet.grpc.customers.LocalDateTime toDto(java.time.LocalDateTime dateTime) {
    if (dateTime == null) {
      return null;
    }
    return sinnet.grpc.customers.LocalDateTime.newBuilder()
        .setYear(dateTime.getYear())
        .setMonth(dateTime.getMonthValue())
        .setDay(dateTime.getDayOfMonth())
        .setHour(dateTime.getHour())
        .setMinute(dateTime.getMinute())
        .setSecond(dateTime.getSecond())
        .build();
  }

  /**
   * TBD.
   */
  static sinnet.grpc.customers.CustomerSecret toDto(CustomerSecret value) {
    return PropsBuilder.build(sinnet.grpc.customers.CustomerSecret.newBuilder())
        .set(Option.of(value.getLocation()).getOrElse("?"), b -> b::setLocation)
        .set(value.getUsername(), b -> b::setUsername)
        .set(value.getPassword(), b -> b::setPassword)
        .set(Option.of(value.getChangedWho().value()).getOrElse("?"), b -> b::setChangedWho)
        .set(toDto(value.getChangedWhen()), b -> b::setChangedWhen)
        .set(value.getOtpSecret(), b -> b::setOtpSecret)
        .set(value.getOtpRecoveryKeys(), b -> b::setOtpRecoveryKeys)
        .done().build();
  }

  /**
   * TBD.
   */
  static sinnet.grpc.customers.CustomerSecretEx toDto(CustomerSecretEx it) {
    return PropsBuilder.build(sinnet.grpc.customers.CustomerSecretEx.newBuilder())
        .set(Option.of(it.getLocation()).getOrElse("?"), b -> b::setLocation)
        .set(it.getUsername(), b -> b::setUsername)
        .set(it.getPassword(), b -> b::setPassword)
        .set(it.getEntityName(), b -> b::setEntityName)
        .set(it.getEntityCode(), b -> b::setEntityCode)
        .set(Option.of(it.getChangedWho().value()).getOrElse("?"), b -> b::setChangedWho)
        .set(toDto(it.getChangedWhen()), b -> b::setChangedWhen)
        .set(it.getOtpSecret(), b -> b::setOtpSecret)
        .set(it.getOtpRecoveryKeys(), b -> b::setOtpRecoveryKeys)
        .done().build();
  }

  /**
   * TBD.
   */
  static LocalDateTime fromDto(sinnet.grpc.customers.LocalDateTime item) {
    return Optional.ofNullable(item)
        .map(it -> LocalDateTime.of(it.getYear(), it.getMonth(), it.getDay(), it.getHour(), it.getMinute(),
            it.getSecond()))
        .orElse(null);
  }

  /**
   * TBD.
   */
  static CustomerContact fromDto(sinnet.grpc.customers.CustomerContact item) {
    return new CustomerContact()
        .setFirstName(item.getFirstName())
        .setLastName(item.getLastName())
        .setPhoneNo(item.getPhoneNo())
        .setEmail(item.getEmail());
  }

  /**
   * TBD.
   */
  static CustomerSecret fromDto(sinnet.grpc.customers.CustomerSecret item) {
    return new CustomerSecret()
        .setLocation(item.getLocation())
        .setPassword(item.getPassword())
        .setUsername(item.getUsername())
        .setChangedWho(ValEmail.of(item.getChangedWho()))
        .setChangedWhen(fromDto(item.getChangedWhen()))
        .setOtpSecret(item.getOtpSecret())
        .setOtpRecoveryKeys(item.getOtpRecoveryKeys());
  }

  /**
   * TBD.
   */
  static CustomerSecretEx fromDto(sinnet.grpc.customers.CustomerSecretEx item) {
    return new CustomerSecretEx()
        .setLocation(item.getLocation())
        .setPassword(item.getPassword())
        .setUsername(item.getUsername())
        .setEntityCode(item.getEntityCode())
        .setEntityName(item.getEntityName())
        .setChangedWho(ValEmail.of(item.getChangedWho()))
        .setChangedWhen(fromDto(item.getChangedWhen()))
        .setOtpSecret(item.getOtpSecret())
        .setOtpRecoveryKeys(item.getOtpRecoveryKeys());

  }

  /**
   * TBD.
   */
  static CustomerModel fromDto(sinnet.grpc.customers.CustomerModel item) {
    return new CustomerModel()
        .setId(Mapper.fromDto(item.getId()))
        .setValue(MapperDto.fromDto(item.getValue()))
        .setContacts(item.getContactsList().stream().map(MapperDto::fromDto).toList())
        .setSecrets(item.getSecretsList().stream().map(MapperDto::fromDto).toList())
        .setSecretsEx(item.getSecretExList().stream().map(MapperDto::fromDto).toList());
  }

  /**
   * TBD.
   */
  static CustomerValue fromDto(sinnet.grpc.customers.CustomerValue dto) {
    if (dto == null) {
      return null;
    }
    return new CustomerValue()
        .setOperatorEmail(ValEmail.of(dto.getOperatorEmail()))
        .setSupportStatus(dto.getSupportStatus())
        .setBillingModel(dto.getBillingModel())
        .setDistance(dto.getDistance())
        .setCustomerName(ValName.of(dto.getCustomerName()))
        .setCustomerCityName(ValName.of(dto.getCustomerCityName()))
        .setCustomerAddress(dto.getCustomerAddress())
        .setNfzUmowa(dto.getNfzUmowa())
        .setNfzMaFilie(dto.getNfzMaFilie())
        .setNfzLekarz(dto.getNfzLekarz())
        .setNfzPolozna(dto.getNfzPolozna())
        .setNfzPielegniarkaSrodowiskowa(dto.getNfzPielegniarkaSrodowiskowa())
        .setNfzMedycynaSzkolna(dto.getNfzMedycynaSzkolna())
        .setNfzTransportSanitarny(dto.getNfzTransportSanitarny())
        .setNfzNocnaPomocLekarska(dto.getNfzNocnaPomocLekarska())
        .setNfzAmbulatoryjnaOpiekaSpecjalistyczna(dto.getNfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .setNfzRehabilitacja(dto.getNfzRehabilitacja())
        .setNfzStomatologia(dto.getNfzStomatologia())
        .setNfzPsychiatria(dto.getNfzPsychiatria())
        .setNfzSzpitalnictwo(dto.getNfzSzpitalnictwo())
        .setNfzProgramyProfilaktyczne(dto.getNfzProgramyProfilaktyczne())
        .setNfzZaopatrzenieOrtopedyczne(dto.getNfzZaopatrzenieOrtopedyczne())
        .setNfzOpiekaDlugoterminowa(dto.getNfzOpiekaDlugoterminowa())
        .setNfzNotatki(dto.getNfzNotatki())
        .setKomercjaJest(dto.getKomercjaJest())
        .setKomercjaNotatki(dto.getKomercjaNotatki())
        .setDaneTechniczne(dto.getDaneTechniczne());
  }

}
