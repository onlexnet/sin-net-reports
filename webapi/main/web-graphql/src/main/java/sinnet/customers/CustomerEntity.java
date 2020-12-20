package sinnet.customers;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.Value;
import sinnet.Entity;
import sinnet.models.CustomerValue;

@Value
public class CustomerEntity {
    private Entity id;
    private CustomerValue optionalValue;
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
                it.getNfzNotatki());
        }
        // TODO resolve if no data provided
        return null;
    }
}

