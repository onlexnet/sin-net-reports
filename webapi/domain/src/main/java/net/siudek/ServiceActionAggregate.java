package net.siudek;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import lombok.NoArgsConstructor;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * Represent group of individual services for a client, done in relatively short
 * period of time (e.g. day). Often all of those services as listen together on
 * an invoice.
 */
@Aggregate
@NoArgsConstructor // constructor needed for reconstruction
public class ServiceActionAggregate {

    @AggregateIdentifier
    private String id;

    @CommandHandler
    public ServiceActionAggregate(RegisterNewServiceCommand cmd) {
        apply(new NewServiceRegistered(cmd.getServiceActionId()));
    }

    @EventSourcingHandler
    public void on(NewServiceRegistered event) {
        id = event.getServiceActionId();
    }
}