package sinnet.customers;

import java.util.concurrent.CompletableFuture;

import com.google.common.base.Objects;

import org.springframework.stereotype.Component;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLResolver;
import sinnet.AskTemplate;
import sinnet.MyEntity;
import sinnet.SomeEntity;
import sinnet.bus.EntityId;
import sinnet.bus.commands.UpdateCustomerInfo;

@Component
public class CustomersMutationSave extends AskTemplate<UpdateCustomerInfo, EntityId>
                                   implements GraphQLResolver<CustomersMutation> {

    protected CustomersMutationSave() {
        super(UpdateCustomerInfo.ADDRESS, EntityId.class);
    }

    CompletableFuture<SomeEntity> save(CustomersMutation gcontext, MyEntity id, CustomerEntry entry) {
        if (!Objects.equal(id.getProjectId(), gcontext.getProjectId())) {
            throw new GraphQLException("Invalid project id");
        }

        var eid = new EntityId(id.getProjectId(), id.getEntityId(), id.getEntityVersion());
        var cmd = UpdateCustomerInfo.builder()
            .id(eid)
            .emailOfOperator(entry.getOperatorEmail())
            .modelOfSupport(entry.getSupportStatus())
            .modelOfBilling(entry.getBillingModel())
            .distance(entry.getDistance())
            .customerName(entry.getCustomerName())
            .customerCityName(entry.getCustomerCityName())
            .customerAddress(entry.getCustomerAddress())
            .nfzUmowa(entry.isNfzUmowa())
            .nfzMaFilie(entry.isNfzMaFilie())
            .nfzLekarz(entry.isNfzLekarz())
            .nfzPolozna(entry.isNfzPolozna())
            .nfzPielegniarkaSrodowiskowa(entry.isNfzPielegniarkaSrodowiskowa())
            .nfzMedycynaSzkolna(entry.isNfzMedycynaSzkolna())
            .nfzTransportSanitarny(entry.isNfzTransportSanitarny())
            .nfzNocnaPomocLekarska(entry.isNfzNocnaPomocLekarska())
            .nfzAmbulatoryjnaOpiekaSpecjalistyczna(entry.isNfzAmbulatoryjnaOpiekaSpecjalistyczna())
            .nfzRehabilitacja(entry.isNfzRehabilitacja())
            .nfzStomatologia(entry.isNfzStomatologia())
            .nfzPsychiatria(entry.isNfzPsychiatria())
            .nfzSzpitalnictwo(entry.isNfzSzpitalnictwo())
            .nfzProgramyProfilaktyczne(entry.isNfzProgramyProfilaktyczne())
            .nfzZaopatrzenieOrtopedyczne(entry.isNfzZaopatrzenieOrtopedyczne())
            .nfzOpiekaDlugoterminowa(entry.isNfzOpiekaDlugoterminowa())
            .nfzNotatki(entry.getNfzNotatki())
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
    }
}

