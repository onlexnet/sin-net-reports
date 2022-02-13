package sinnet.gql.customers;

import java.util.UUID;

import io.vavr.collection.Array;
import io.vavr.control.Option;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomers;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.CustomerContact;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomerSecret;
import sinnet.grpc.customers.CustomerSecretEx;
import sinnet.grpc.customers.CustomerValue;
import sinnet.grpc.customers.LocalDateTime;
import sinnet.models.Name;

public interface Mapper {

    default CustomerModel toDto(UUID projectId, FindCustomer.Reply reply) {
        return CustomerModel.newBuilder()
            .setId(PropsBuilder.build(EntityId.newBuilder())
                .set(projectId.toString(), b -> b::setProjectId)
                .set(reply.getEntityId().toString(), b -> b::setEntityId)
                .set(reply.getEntityVersion(), b -> b::setEntityVersion)
                .done().build())
            .setValue(map(reply.getValue()))
            .addAllSecrets(map(reply.getSecrets()))
            .addAllSecretEx(map(reply.getSecretsEx()))
            .addAllContacts(map(reply.getContacts()))
            .build();
      }
    
      default CustomerModel toDto(FindCustomers.CustomerData it) {
        return CustomerModel.newBuilder()
            .setId(PropsBuilder.build(EntityId.newBuilder())
                .set(it.getEntityId().toString(), b -> b::setEntityId)
                .set(it.getEntityVersion(), b -> b::setEntityVersion)
                .done().build())
            .setValue(map(it.getValue()))
            .addAllSecrets(map(it.getSecrets()))
            .addAllSecretEx(map(it.getSecretsEx()))
            .addAllContacts(map(it.getContacts()))
            .build();
      }

      default sinnet.models.CustomerValue fromDto(CustomerValue dto) {
        if (dto == null) return null;
        return sinnet.models.CustomerValue.builder()
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
          .daneTechniczne(dto.getDaneTechniczne())
          .build();
      }

      private static CustomerValue map(sinnet.models.CustomerValue value) {
        return PropsBuilder.build(CustomerValue.newBuilder())
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
        return Array.of(value).map(Mapper::map);
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
        return Array.of(value).map(Mapper::map);
      }
    
      private static LocalDateTime map(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return  LocalDateTime.newBuilder()
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
        return Array.of(value).map(Mapper::map);
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
