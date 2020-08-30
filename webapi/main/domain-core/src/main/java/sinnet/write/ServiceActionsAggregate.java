package sinnet.write;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.modelling.command.AggregateCreationPolicy;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateMember;
import org.axonframework.modelling.command.CreationPolicy;
import org.axonframework.modelling.command.EntityId;
import org.axonframework.spring.stereotype.Aggregate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sinnet.RegisterNewServiceAction;
import sinnet.events.NewServiceActionRegistered;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represent group of individual services for a client, done in relatively
 * short period of time (e.g. day). Often all of those services as listen
 * together on an invoice.
 */
@Aggregate
@NoArgsConstructor // constructor needed for reconstruction
public class ServiceActionsAggregate {

    /** Unique identifier of the aggreagte. */
    @AggregateIdentifier
    private String id;

    @AggregateMember
    private List<ServiceAction> actions = new ArrayList<>();

    /**
     * FixMe.
     *
     * @param cmd fixme
     */
    @CommandHandler
    @CreationPolicy(AggregateCreationPolicy.CREATE_IF_MISSING)
    public void when(final RegisterNewServiceAction cmd, @MetaDataValue("correlactionId") UUID correlactionId) {
        var evt = NewServiceActionRegistered.builder()
                    .id(cmd.getServiceActionId())
                    .when(cmd.getWhen())
                    .description(cmd.getWhat())
                    .build();
        apply(evt, MetaData.with("correlactionId", correlactionId));
    }

    /**
     * FixMe.
     *
     * @param event fixme
     */
    @EventSourcingHandler
    public void on(final NewServiceActionRegistered event) {
        id = event.getId();
    }
}

class ServiceAction {

    @EntityId
    @Getter
    private String serviceActionId;

    ServiceAction(String serviceActionId) {
    }

}
