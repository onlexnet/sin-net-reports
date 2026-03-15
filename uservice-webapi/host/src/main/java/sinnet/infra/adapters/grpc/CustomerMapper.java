package sinnet.infra.adapters.grpc;

import java.time.LocalDateTime;
import java.util.UUID;

import sinnet.app.flow.request.CustomerGetResult;
import sinnet.domain.models.Customer;
import sinnet.domain.models.CustomerContact;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerSecret;
import sinnet.domain.models.CustomerValue;
import sinnet.gql.models.EntityGql;
import sinnet.gql.utils.PropsBuilder;
import sinnet.grpc.common.EntityId;

public interface CustomerMapper {

  static CustomerGetResult toGetResult(sinnet.grpc.customers.CustomerModel item) {
    if (item == null) {
      return null;
    }
    return new CustomerGetResult(
        new sinnet.domain.models.EntityId(
            UUID.fromString(item.getId().getProjectId()),
            UUID.fromString(item.getId().getEntityId()),
            item.getId().getEntityVersion()),
        toDomain(item.getValue(), item.getSecretsList(), item.getSecretExList(), item.getContactsList()));
  }

  static Customer toDomain(sinnet.grpc.customers.CustomerModel item) {
    if (item == null) {
      return null;
    }
    return new Customer(
        new sinnet.domain.models.EntityId(
            UUID.fromString(item.getId().getProjectId()),
            UUID.fromString(item.getId().getEntityId()),
            item.getId().getEntityVersion()),
        toDomain(item.getValue(), item.getSecretsList(), item.getSecretExList(), item.getContactsList()));
  }

  static LocalDateTime toDomain(sinnet.grpc.customers.LocalDateTime it) {
    if (it == null || it.getMonth() == 0) {
      return null;
    }
    return LocalDateTime.of(it.getYear(), it.getMonth(), it.getDay(), it.getHour(), it.getMinute(), it.getSecond());
  }

  static CustomerEntry toEntry(sinnet.grpc.customers.CustomerValue item) {
    if (item == null) {
      return null;
    }
    return new CustomerEntry(
        item.getOperatorEmail(),
        item.getBillingModel(),
        item.getSupportStatus(),
        item.getDistance(),
        item.getCustomerName(),
        item.getCustomerCityName(),
        item.getCustomerAddress(),
        item.getNfzUmowa(),
        item.getNfzMaFilie(),
        item.getNfzLekarz(),
        item.getNfzPolozna(),
        item.getNfzPielegniarkaSrodowiskowa(),
        item.getNfzMedycynaSzkolna(),
        item.getNfzTransportSanitarny(),
        item.getNfzNocnaPomocLekarska(),
        item.getNfzAmbulatoryjnaOpiekaSpecjalistyczna(),
        item.getNfzRehabilitacja(),
        item.getNfzStomatologia(),
        item.getNfzPsychiatria(),
        item.getNfzSzpitalnictwo(),
        item.getNfzProgramyProfilaktyczne(),
        item.getNfzZaopatrzenieOrtopedyczne(),
        item.getNfzOpiekaDlugoterminowa(),
        item.getNfzNotatki(),
        item.getKomercjaJest(),
        item.getKomercjaNotatki(),
        item.getDaneTechniczne());
  }

  static CustomerValue toDomain(sinnet.grpc.customers.CustomerValue item,
                                java.util.List<sinnet.grpc.customers.CustomerSecret> secrets,
                                java.util.List<sinnet.grpc.customers.CustomerSecretEx> secretsEx,
                                java.util.List<sinnet.grpc.customers.CustomerContact> contacts) {
    if (item == null) {
      return null;
    }
    return new CustomerValue(
        toEntry(item),
        secrets.stream().map(CustomerMapper::toDomain).toList(),
        secretsEx.stream().map(CustomerMapper::toDomain).toList(),
        contacts.stream().map(CustomerMapper::toDomain).toList());
  }

  static sinnet.domain.models.CustomerSecretEx toDomain(sinnet.grpc.customers.CustomerSecretEx it) {
    if (it == null) {
      return null;
    }
    return new sinnet.domain.models.CustomerSecretEx(
        it.getLocation(),
        it.getUsername(),
        it.getPassword(),
        it.getEntityName(),
        it.getEntityCode(),
        it.getOtpSecret(),
        it.getOtpRecoveryKeys());
  }

  static CustomerSecret toDomain(sinnet.grpc.customers.CustomerSecret it) {
    if (it == null) {
      return null;
    }
    return new CustomerSecret(
        it.getLocation(),
        it.getUsername(),
        it.getPassword(),
        it.getOtpSecret(),
        it.getOtpRecoveryKeys());
  }

  static CustomerContact toDomain(sinnet.grpc.customers.CustomerContact it) {
    if (it == null) {
      return null;
    }
    return new CustomerContact(
        it.getFirstName(),
        it.getLastName(),
        it.getPhoneNo(),
        it.getEmail());
  }

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
