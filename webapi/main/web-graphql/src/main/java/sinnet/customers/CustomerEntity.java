package sinnet.customers;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.control.Option;
import lombok.Value;
import sinnet.Entity;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;

@Value
public class CustomerEntity {
    private Entity id;
    private CustomerValue optionalValue;
    private CustomerSecret[] optionalSecrets;
    private CustomerSecretEx[] optionalSecretsEx;
    private CustomerContact[] optionalContacts;
}

@Component
class CustomerModelResolverPayload implements GraphQLResolver<CustomerEntity> {
    CustomerModel getData(CustomerEntity gcontext) {
        if (gcontext.getOptionalValue() != null) {
            var it = gcontext.getOptionalValue();
            return new CustomerModel(
                it.getOperatorEmail(),
                it.getBillingModel(),
                it.getSupportStatus(),
                it.getDistance(),
                it.getCustomerName().getValue(),
                it.getCustomerCityName().getValue(),
                it.getCustomerAddress(),
                it.isNfzUmowa(),
                it.isNfzMaFilie(),
                it.isNfzLekarz(),
                it.isNfzPolozna(),
                it.isNfzPielegniarkaSrodowiskowa(),
                it.isNfzMedycynaSzkolna(),
                it.isNfzTransportSanitarny(),
                it.isNfzNocnaPomocLekarska(),
                it.isNfzAmbulatoryjnaOpiekaSpecjalistyczna(),
                it.isNfzRehabilitacja(),
                it.isNfzStomatologia(),
                it.isNfzPsychiatria(),
                it.isNfzSzpitalnictwo(),
                it.isNfzProgramyProfilaktyczne(),
                it.isNfzZaopatrzenieOrtopedyczne(),
                it.isNfzOpiekaDlugoterminowa(),
                it.getNfzNotatki(),
                it.isKomercjaJest(),
                it.getKomercjaNotatki(),
                it.getDaneTechniczne());
        }
        // TODO resolve if no data provided
        return null;
    }

    private static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    sinnet.customers.CustomerSecret[] getSecrets(CustomerEntity gcontext) {
        if (gcontext.getOptionalSecrets() != null) {
            return Arrays.stream(gcontext.getOptionalSecrets())
                .map(it -> new sinnet.customers.CustomerSecret(
                    Option.of(it.getLocation()).getOrElse("?"),
                    it.getUsername(),
                    it.getPassword(),
                    Option.of(it.getChangedWho().getValue()).getOrElse("?"),
                    Option.of(it.getChangedWhen()).map(v -> timestampFormatter.format(v)).getOrElse("?")))
                .toArray(sinnet.customers.CustomerSecret[]::new);
        }
        // TODO resolve if no data provided
        return null;
    }

    sinnet.customers.CustomerSecretEx[] getSecretsEx(CustomerEntity gcontext) {
        if (gcontext.getOptionalSecretsEx() != null) {
            return Arrays.stream(gcontext.getOptionalSecretsEx())
                .map(it -> new sinnet.customers.CustomerSecretEx(
                    Option.of(it.getLocation()).getOrElse("?"),
                    it.getUsername(),
                    it.getPassword(),
                    it.getEntityName(),
                    it.getEntityCode(),
                    Option.of(it.getChangedWho().getValue()).getOrElse("?"),
                    Option.of(it.getChangedWhen()).map(v -> timestampFormatter.format(v)).getOrElse("?")))
                .toArray(sinnet.customers.CustomerSecretEx[]::new);
        }
        // TODO resolve if no data provided
        return null;
    }

    sinnet.customers.CustomerContact[] getContacts(CustomerEntity gcontext) {
        if (gcontext.getOptionalContacts() != null) {
            return Arrays.stream(gcontext.getOptionalContacts())
                .map(it -> new sinnet.customers.CustomerContact(
                    it.getFirstName(),
                    it.getLastName(),
                    it.getPhoneNo(),
                    it.getEmail()))
                .toArray(sinnet.customers.CustomerContact[]::new);
        }
        // TODO resolve if no data provided
        return null;
    }
}

