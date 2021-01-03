package sinnet.customers;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.GraphQLException;
import graphql.kickstart.tools.GraphQLResolver;
import sinnet.AskTemplate;
import sinnet.IdentityProvider;
import sinnet.MyEntity;
import sinnet.SomeEntity;
import sinnet.bus.EntityId;
import sinnet.bus.commands.ChangeCustomer;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.Name;

@Component
public class CustomersMutationSave extends AskTemplate<ChangeCustomer.Command, EntityId>
                                   implements GraphQLResolver<CustomersMutation> {

    @Autowired
    private IdentityProvider identityProvider;

    public CustomersMutationSave() {
        super(ChangeCustomer.Command.ADDRESS, EntityId.class);
    }

    CompletableFuture<SomeEntity> save(CustomersMutation gcontext, MyEntity id, CustomerInput entry,
                                                                                CustomerSecret[] secrets,
                                                                                CustomerSecretEx[] secretsEx,
                                                                                CustomerContact[] contacts) {
        var maybeRequestor = identityProvider.getCurrent();
        if (!maybeRequestor.isPresent()) throw new GraphQLException("Access denied");

        if (!Objects.equal(id.getProjectId(), gcontext.getProjectId())) {
            throw new GraphQLException("Invalid project id");
        }

        var eid = new EntityId(id.getProjectId(), id.getEntityId(), id.getEntityVersion());
        var value = CustomerValue.builder()
            .operatorEmail(entry.getOperatorEmail())
            .supportStatus(entry.getSupportStatus())
            .billingModel(entry.getBillingModel())
            .distance(entry.getDistance())
            .customerName(Name.of(entry.getCustomerName()))
            .customerCityName(Name.of(entry.getCustomerCityName()))
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
            .komercjaJest(entry.isKomercjaJest())
            .komercjaNotatki(entry.getKomercjaNotatki())
            .build();
        var mSecrets = Arrays
            .stream(secrets)
            .map(it -> ChangeCustomer.Secret.builder()
                .location(it.getLocation())
                .username(it.getUsername())
                .password(it.getPassword())
                .build())
            .toArray(ChangeCustomer.Secret[]::new);
        var mSecretsEx = Arrays
            .stream(secretsEx)
            .map(it -> ChangeCustomer.SecretEx.builder()
                .location(it.getLocation())
                .username(it.getUsername())
                .password(it.getPassword())
                .entityName(it.getEntityName())
                .entityCode(it.getEntityCode())
                .build())
            .toArray(ChangeCustomer.SecretEx[]::new);
        var mContacts = Arrays
            .stream(contacts)
            .map(it -> ChangeCustomer.Contact.builder()
                .name(it.getName())
                .surname(it.getSurname())
                .phoneNo(it.getPhoneNo())
                .email(it.getEmail())
                .build())
            .toArray(ChangeCustomer.Contact[]::new);

        var emailOfRequestor = maybeRequestor.get().getEmail();
        var cmd = ChangeCustomer.Command.builder()
            .requestor(Email.of(emailOfRequestor))
            .id(eid)
            .value(value)
            .secrets(mSecrets)
            .secretsEx(mSecretsEx)
            .contacts(mContacts)
            .build();
        return super.ask(cmd).thenApply(m -> new SomeEntity(m.getProjectId(), m.getId(), m.getVersion()));
    }
}

