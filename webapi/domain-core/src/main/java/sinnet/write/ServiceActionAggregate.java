package sinnet.write;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import lombok.NoArgsConstructor;
import sinnet.RegisterNewServiceAction;
import sinnet.events.NewServiceRegistered;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * Represent group of individual services for a client, done in relatively
 * short period of time (e.g. day). Often all of those services as listen
 * together on an invoice.
 */
@Aggregate
@NoArgsConstructor // constructor needed for reconstruction
public class ServiceActionAggregate {

    /** Unique identifier of the aggreagte. */
    @AggregateIdentifier
    private String id;

    /**
     * FixMe.
     *
     * @param cmd fixme
     */
    @CommandHandler
    public ServiceActionAggregate(final RegisterNewServiceAction cmd) {
        var evt = NewServiceRegistered.builder()
                    .id(cmd.getServiceActionId())
                    .when(cmd.getWhen())
                    .build();
        apply(evt);
    }

    /**
     * FixMe.
     *
     * @param event fixme
     */
    @EventSourcingHandler
    public void on(final NewServiceRegistered event) {
        id = event.getId();
    }
}
