package sinnet.gql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import io.vavr.collection.Iterator;
import io.vavr.control.Option;
import sinnet.CustomerContact;
import sinnet.CustomerEntity;
import sinnet.CustomerModel;
import sinnet.CustomerSecret;
import sinnet.CustomerSecretEx;
import sinnet.Entity;
import sinnet.grpc.customers.GetReply;

public interface CustomerMapper extends Mapper {

    default CustomerEntity toGql(GetReply dto) {
        if (dto == null) return null;
        var item = dto.getModel();
        if (item == null) return null;
        var result = new CustomerEntity();
        result.setId(toGql(item.getId()));
        result.setData(toGql(item.getValue()));
        result.setSecrets(Iterator.ofAll(item.getSecretsList()).map(this::toGql).toJavaArray(CustomerSecret[]::new));
        result.setSecretsEx(Iterator.ofAll(item.getSecretExList()).map(this::toGql).toJavaArray(CustomerSecretEx[]::new));
        result.setContacts(Iterator.ofAll(item.getContactsList()).map(this::toGql).toJavaArray(CustomerContact[]::new));
        return result;
    }

    default Entity toGql(sinnet.grpc.common.EntityId item) {
        if (item == null) return null;
        var result = new Entity();
        result.setProjectId(item.getProjectId());
        result.setEntityId(item.getEntityId());
        result.setEntityVersion(item.getEntityVersion());
        return result;
    }

    default CustomerModel toGql(sinnet.grpc.customers.CustomerValue item) {
        if (item == null) return null;
        var it = new CustomerModel();
        it.setOperatorEmail(item.getOperatorEmail());
        it.setBillingModel(item.getBillingModel());
        it.setSupportStatus(item.getSupportStatus());
        it.setDistance(item.getDistance());
        it.setCustomerName(item.getCustomerName());
        it.setCustomerCityName(item.getCustomerCityName());
        it.setCustomerAddress(item.getCustomerAddress());
        it.setNfzUmowa(item.getNfzUmowa());
        it.setNfzMaFilie(item.getNfzMaFilie());
        it.setNfzLekarz(item.getNfzLekarz());
        it.setNfzPolozna(item.getNfzPolozna());
        it.setNfzPielegniarkaSrodowiskowa(item.getNfzPielegniarkaSrodowiskowa());
        it.setNfzMedycynaSzkolna(item.getNfzMedycynaSzkolna());
        it.setNfzTransportSanitarny(item.getNfzTransportSanitarny());
        it.setNfzNocnaPomocLekarska(item.getNfzNocnaPomocLekarska());
        it.setNfzAmbulatoryjnaOpiekaSpecjalistyczna(item.getNfzAmbulatoryjnaOpiekaSpecjalistyczna());
        it.setNfzRehabilitacja(item.getNfzRehabilitacja());
        it.setNfzStomatologia(item.getNfzStomatologia());
        it.setNfzPsychiatria(item.getNfzPsychiatria());
        it.setNfzSzpitalnictwo(item.getNfzSzpitalnictwo());
        it.setNfzProgramyProfilaktyczne(item.getNfzProgramyProfilaktyczne());
        it.setNfzZaopatrzenieOrtopedyczne(item.getNfzZaopatrzenieOrtopedyczne());
        it.setNfzOpiekaDlugoterminowa(item.getNfzOpiekaDlugoterminowa());
        it.setNfzNotatki(item.getNfzNotatki());
        it.setKomercjaJest(item.getKomercjaJest());
        it.setKomercjaNotatki(item.getKomercjaNotatki());
        it.setDaneTechniczne(item.getDaneTechniczne());
        return it;
    }

    // DateTime, created and kept on server-side is UTC only. We can't send data to
    // client because GraphQL does not support Date / Time types.
    // So, we send a simple string without implicit contratc so that client may
    // convert them so its
    // proper models for date/time representation
    // reason: https://github.com/onlexnet/sin-net-reports/issues/59
    static DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_DATE_TIME;

    default LocalDateTime map(sinnet.grpc.customers.LocalDateTime it) {
        if (it == null) return null;
        return LocalDateTime.of(it.getYear(), it.getMonth(), it.getDay(), it.getHour(), it.getMinute(), it.getSecond());
    }

    default CustomerSecret toGql(sinnet.grpc.customers.CustomerSecret it) {
        if (it == null) return null;
        var result = new CustomerSecret();
        result.setLocation(it.getLocation());
        result.setUsername(it.getUsername());
        result.setPassword(it.getPassword());
        result.setChangedWhen(Option.of(map(it.getChangedWhen())).map(timestampFormatter::format).getOrElse("?"));
        result.setChangedWho(it.getChangedWho());
        return result;
    }

    default CustomerSecretEx toGql(sinnet.grpc.customers.CustomerSecretEx it) {
        if (it == null) return null;
        var result = new CustomerSecretEx();
        result.setLocation(it.getLocation());
        result.setUsername(it.getUsername());
        result.setPassword(it.getPassword());
        result.setEntityCode(it.getEntityCode());
        result.setEntityName(it.getEntityName());
        result.setChangedWhen(Option.of(map(it.getChangedWhen())).map(timestampFormatter::format).getOrElse("?"));
        result.setChangedWho(it.getChangedWho());
        return result;
    }

    default CustomerContact toGql(sinnet.grpc.customers.CustomerContact it) {
        if (it == null) return null;
        var result = new CustomerContact();
        result.setFirstName(it.getFirstName());
        result.setLastName(it.getLastName());
        result.setPhoneNo(it.getPhoneNo());
        result.setEmail(it.getEmail());      
        return result;
    }
}
