package sinnet.customers;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.Value;
import sinnet.Entity;
import sinnet.models.CustomerAuthorization;
import sinnet.models.CustomerValue;

@Value
public class CustomerEntity {
    private Entity id;
    private CustomerValue optionalValue;
    private CustomerAuthorization[] optionalAuthorizations;
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
                it.getKomercjaNotatki());
        }
        // TODO resolve if no data provided
        return null;
    }

    sinnet.customers.CustomerAuthorization[] getAuthorizations(CustomerEntity gcontext) {
        if (gcontext.getOptionalAuthorizations() != null) {
            return Arrays.stream(gcontext.getOptionalAuthorizations())
                .map(it -> new sinnet.customers.CustomerAuthorization(it.getLocation(),
                    it.getUsername(),
                    it.getPassword(),
                    it.getChangedWho().getValue(),
                    it.getChangedWhen().toString()))
                .toArray(sinnet.customers.CustomerAuthorization[]::new);
        }
        // TODO resolve if no data provided
        return null;
    }

}

