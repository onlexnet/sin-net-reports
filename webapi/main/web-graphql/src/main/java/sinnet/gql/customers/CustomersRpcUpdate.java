package sinnet.gql.customers;

import org.springframework.stereotype.Component;

import io.grpc.stub.StreamObserver;
import io.vertx.core.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import sinnet.bus.AskTemplate;
import sinnet.bus.commands.ChangeCustomerData;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.customers.UpdateResult;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.models.Name;
import sinnet.vertx.Handlers;

@Component
@Slf4j
public class CustomersRpcUpdate extends AskTemplate<ChangeCustomerData.Command, EntityId>
                                implements sinnet.gql.common.Mapper {
                                   

  public CustomersRpcUpdate(EventBus eventBus) {
    super(ChangeCustomerData.Command.ADDRESS, EntityId.class, eventBus);
  }

  void command(UpdateCommand cmd, StreamObserver<UpdateResult> streamObserver) {

    var emailOfRequestor = Email.of(cmd.getUserToken().getRequestorEmail());
    var entry = cmd.getModel().getValue();
    var eid = fromDto(cmd.getModel().getId());
    var value = CustomerValue.builder()
        .operatorEmail(entry.getOperatorEmail())
        .supportStatus(entry.getSupportStatus())
        .billingModel(entry.getBillingModel())
        .distance(entry.getDistance())
        .customerName(Name.of(entry.getCustomerName()))
        .customerCityName(Name.of(entry.getCustomerCityName()))
        .customerAddress(entry.getCustomerAddress())
        .nfzUmowa(entry.getNfzUmowa())
        .nfzMaFilie(entry.getNfzMaFilie())
        .nfzLekarz(entry.getNfzLekarz())
        .nfzPolozna(entry.getNfzPolozna())
        .nfzPielegniarkaSrodowiskowa(entry.getNfzPielegniarkaSrodowiskowa())
        .nfzMedycynaSzkolna(entry.getNfzMedycynaSzkolna())
        .nfzTransportSanitarny(entry.getNfzTransportSanitarny())
        .nfzNocnaPomocLekarska(entry.getNfzNocnaPomocLekarska())
        .nfzAmbulatoryjnaOpiekaSpecjalistyczna(entry.getNfzAmbulatoryjnaOpiekaSpecjalistyczna())
        .nfzRehabilitacja(entry.getNfzRehabilitacja())
        .nfzStomatologia(entry.getNfzStomatologia())
        .nfzPsychiatria(entry.getNfzPsychiatria())
        .nfzSzpitalnictwo(entry.getNfzSzpitalnictwo())
        .nfzProgramyProfilaktyczne(entry.getNfzProgramyProfilaktyczne())
        .nfzZaopatrzenieOrtopedyczne(entry.getNfzZaopatrzenieOrtopedyczne())
        .nfzOpiekaDlugoterminowa(entry.getNfzOpiekaDlugoterminowa())
        .nfzNotatki(entry.getNfzNotatki())
        .komercjaJest(entry.getKomercjaJest())
        .komercjaNotatki(entry.getKomercjaNotatki())
        .daneTechniczne(entry.getDaneTechniczne())
        .build();
    var secrets = cmd.getModel().getSecretsList();
    var mSecrets = secrets.stream()
        .map(it -> ChangeCustomerData.Secret.builder()
            .location(it.getLocation())
            .username(it.getUsername())
            .password(it.getPassword())
            .build())
        .toArray(ChangeCustomerData.Secret[]::new);
    var secretsEx = cmd.getModel().getSecretExList();
    var mSecretsEx = secretsEx.stream()
        .map(it -> ChangeCustomerData.SecretEx.builder()
            .location(it.getLocation())
            .username(it.getUsername())
            .password(it.getPassword())
            .entityName(it.getEntityName())
            .entityCode(it.getEntityCode())
            .build())
        .toArray(ChangeCustomerData.SecretEx[]::new);
    var contacts = cmd.getModel().getContactsList();
    var mContacts = contacts.stream()
        .map(it -> ChangeCustomerData.Contact.builder()
            .firstName(it.getFirstName())
            .lastName(it.getLastName())
            .phoneNo(it.getPhoneNo())
            .email(it.getEmail())
            .build())
        .toArray(ChangeCustomerData.Contact[]::new);

    var mCmd = ChangeCustomerData.Command.builder()
        .requestor(emailOfRequestor)
        .id(eid)
        .value(value)
        .secrets(mSecrets)
        .secretsEx(mSecretsEx)
        .contacts(mContacts)
        .build();

    super.ask(mCmd).onComplete(Handlers.logged(log, streamObserver, it -> {
        var result = toDto(it);
        return UpdateResult.newBuilder()
            .setEntityId(result)
            .build();
    }));
  }
}

