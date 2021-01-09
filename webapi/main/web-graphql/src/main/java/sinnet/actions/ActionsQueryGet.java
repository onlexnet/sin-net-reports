package sinnet.actions;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.control.Option;
import lombok.Value;
import sinnet.ActionRepository;
import sinnet.SimpleMediatorRunner;
import sinnet.models.ActionValue;
import sinnet.models.Entity;
import sinnet.read.CustomerProjection;

/** Fixme. */
@Component
public class ActionsQueryGet implements GraphQLResolver<ActionsQuery> {

    @Autowired
    private ActionRepository actionService;

    @Autowired
    private CustomerProjection customerReader;

    @Autowired
    private SimpleMediatorRunner smr;

    public CompletionStage<ServiceModel> get(ActionsQuery gcontext, UUID actionId) {
        var result = new CompletableFuture<ServiceModel>();
        var projectId = gcontext.getProjectId();
        var initialMessage = new MyRequestHandler.Initial(projectId, actionId);
        smr.spawn(MyRequestHandler.create(result, actionService, customerReader), initialMessage);
        return result;
    }
}

final class MyRequestHandler {

    /** Utility classes should not have a public or default constructor. */
    private MyRequestHandler() {
    }

    /**
     * Method to create initial instance of the behavior.
     * @return Behavior of just created instance.
     */
    public static Behavior<MyRequestHandler.Event> create(
                  CompletableFuture<ServiceModel> resultHandler,
                  ActionRepository service1,
                  CustomerProjection service2) {
        return getActionData(service1, service2, resultHandler);
    }

    interface Event {
    }

    @Value
    static class Initial implements Event {
        private UUID projectId;
        private UUID entityId;
    }

    @Value
    static class Error implements Event {
    }

    @Value
    static class OnActionData implements Event {
        private Entity<ActionValue> entity;
    }

    @Value
    static class OnCustomerData implements Event {
        private Entity<ActionValue> actionData;
        private Option<CustomerProjection.CustomerModel> customerData;
    }

    private static Behavior<Event> getActionData(ActionRepository service1,
                   CustomerProjection service2,
                   CompletableFuture<ServiceModel> resultHandler) {
        return Behaviors.setup(ctx ->
            Behaviors
                .receive(Event.class)
                .onMessage(Error.class, it -> {
                    resultHandler.completeExceptionally(new Exception("error"));
                    return Behaviors.stopped();
                })
                .onMessage(Initial.class, it -> {
                    var projectId = it.getProjectId();
                    var entityId = it.getEntityId();
                    var r = service1.find(projectId, entityId).toCompletionStage();
                    ctx.pipeToSelf(r, (ok, exc) -> exc == null
                        ? new OnActionData(ok)
                        : new Error());
                    return Behaviors.same();
                })
                .onMessage(OnActionData.class, it -> {
                    var projectId = it.getEntity().getProjectId();
                    var customerId = it.getEntity().getValue().getWhom();
                    var r = service2.get(projectId, customerId).toCompletionStage();
                    ctx.pipeToSelf(r, (ok, exc) -> exc == null
                        ? new OnCustomerData(it.getEntity(), ok)
                        : new Error());
                    return Behaviors.same();
                })
                .onMessage(OnCustomerData.class, it -> {
                    var actionEntity = it.actionData;
                    var maybeCustomerData = it.getCustomerData();
                    if (maybeCustomerData.isDefined()) {
                        var customerData = maybeCustomerData.get();
                        var result = ServiceModel.builder()
                            .projectId(actionEntity.getProjectId())
                            .entityId(actionEntity.getEntityId())
                            .entityVersion(actionEntity.getVersion())
                            .servicemanName(actionEntity.getValue().getWho().getValue())
                            .whenProvided(actionEntity.getValue().getWhen())
                            .forWhatCustomer(customerData.getValue().getCustomerName().getValue())
                            .description(actionEntity.getValue().getWhat())
                            .duration(actionEntity.getValue().getHowLong().getValue())
                            .distance(actionEntity.getValue().getHowFar().getValue())
                            .build();
                        resultHandler.completeAsync(() -> result);
                    } else {
                        resultHandler.completeExceptionally(new Exception("error"));
                    }
                    return Behaviors.stopped();
                })
                .build());
      }
}
