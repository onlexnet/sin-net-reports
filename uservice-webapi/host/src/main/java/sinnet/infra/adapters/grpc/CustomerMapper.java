package sinnet.infra.adapters.grpc;

import java.time.LocalDateTime;

import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.gql.models.EntityGql;
import sinnet.gql.utils.PropsBuilder;
import sinnet.grpc.common.EntityId;

public interface CustomerMapper {

  static EntityId toGrpc(EntityGql item) {
    return EntityId.newBuilder()
        .setEntityId(item.getEntityId())
        .setProjectId(item.getProjectId())
        .setEntityVersion(item.getEntityVersion())
        .build();
  }

  static sinnet.grpc.customers.CustomerValue toGrpc(CustomerEntry item) {
    if (item == null) {
      return null;
    }
    return PropsBuilder.build(sinnet.grpc.customers.CustomerValue.newBuilder())
        .set(b -> b::setOperatorEmail, item.operatorEmail())
        .set(b -> b::setBillingModel, item.billingModel())
        .set(b -> b::setSupportStatus, item.supportStatus())
        .set(b -> b::setDistance, item.distance())
        .set(b -> b::setCustomerName, item.customerName())
        .set(b -> b::setCustomerCityName, item.customerCityName())
        .set(b -> b::setCustomerAddress, item.customerAddress())
        .set(b -> b::setNfzUmowa, item.nfzUmowa())
        .set(b -> b::setNfzMaFilie, item.nfzMaFilie())
        .set(b -> b::setNfzLekarz, item.nfzLekarz())
        .set(b -> b::setNfzPolozna, item.nfzPolozna())
        .set(b -> b::setNfzPielegniarkaSrodowiskowa, item.nfzPielegniarkaSrodowiskowa())
        .set(b -> b::setNfzMedycynaSzkolna, item.nfzMedycynaSzkolna())
        .set(b -> b::setNfzTransportSanitarny, item.nfzTransportSanitarny())
        .set(b -> b::setNfzNocnaPomocLekarska, item.nfzNocnaPomocLekarska())
        .set(b -> b::setNfzAmbulatoryjnaOpiekaSpecjalistyczna, item.nfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .set(b -> b::setNfzRehabilitacja, item.nfzRehabilitacja())
        .set(b -> b::setNfzStomatologia, item.nfzStomatologia())
        .set(b -> b::setNfzPsychiatria, item.nfzPsychiatria())
        .set(b -> b::setNfzSzpitalnictwo, item.nfzSzpitalnictwo())
        .set(b -> b::setNfzProgramyProfilaktyczne, item.nfzProgramyProfilaktyczne())
        .set(b -> b::setNfzZaopatrzenieOrtopedyczne, item.nfzZaopatrzenieOrtopedyczne())
        .set(b -> b::setNfzOpiekaDlugoterminowa, item.nfzOpiekaDlugoterminowa())
        .set(b -> b::setNfzNotatki, item.nfzNotatki())
        .set(b -> b::setKomercjaJest, item.komercjaJest())
        .set(b -> b::setKomercjaNotatki, item.komercjaNotatki())
        .set(b -> b::setDaneTechniczne, item.daneTechniczne())
        .done().build();
  }

  static sinnet.grpc.customers.CustomerSecretEx toGrpc(sinnet.domain.models.CustomerSecretEx it,
                                                       LocalDateTime whenChanged,
                                                       String whoChanged) {
    if (it == null) {
      return null;
    }
    return PropsBuilder.build(sinnet.grpc.customers.CustomerSecretEx.newBuilder())
        .set(b -> b::setLocation, it.location())
        .set(b -> b::setUsername, it.username())
        .set(b -> b::setPassword, it.password())
        .set(b -> b::setEntityName, it.entityName())
        .set(b -> b::setEntityCode, it.entityCode())
        .set(b -> b::setChangedWhen, toGrpc(whenChanged))
        .set(b -> b::setOtpSecret, it.otpSecret())
        .set(b -> b::setOtpRecoveryKeys, it.otpRecoveryKeys())
        .set(b -> b::setChangedWho, whoChanged)
        .done().build();
  }

  static sinnet.grpc.customers.CustomerSecret toGrpc(CustomerSecret it, LocalDateTime whenChanged, String whoChanged) {
    if (it == null) {
      return null;
    }
    return PropsBuilder.build(sinnet.grpc.customers.CustomerSecret.newBuilder())
        .set(b -> b::setLocation, it.location())
        .set(b -> b::setUsername, it.username())
        .set(b -> b::setPassword, it.password())
        .set(b -> b::setChangedWhen, toGrpc(whenChanged))
        .set(b -> b::setChangedWho, whoChanged)
        .set(b -> b::setOtpSecret, it.otpSecret())
        .set(b -> b::setOtpRecoveryKeys, it.otpRecoveryKeys())
        .done().build();
  }

  static sinnet.grpc.customers.LocalDateTime toGrpc(LocalDateTime from) {
    if (from == null) {
      return null;
    }
    return sinnet.grpc.customers.LocalDateTime.newBuilder()
        .setYear(from.getYear())
        .setMonth(from.getMonthValue())
        .setDay(from.getDayOfMonth())
        .setHour(from.getHour())
        .setMinute(from.getMinute())
        .setSecond(from.getSecond())
        .build();
  }

  static sinnet.grpc.customers.CustomerContact toGrpc(CustomerContact it) {
    if (it == null) {
      return null;
    }
    return PropsBuilder.build(sinnet.grpc.customers.CustomerContact.newBuilder())
        .set(b -> b::setFirstName, it.firstName())
        .set(b -> b::setLastName, it.lastName())
        .set(b -> b::setPhoneNo, it.phoneNo())
        .set(b -> b::setEmail, it.email())
        .done().build();
  }
}
