package sinnet.features;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import sinnet.domain.model.ValEmail;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.UpdateCommand;
import sinnet.grpc.timeentries.LocalDate;
import sinnet.models.ProjectId;
import sinnet.models.ValName;


@Configuration
class TestBeans {

  @Bean
  ClientContext clientContext() {
    return new ClientContext();
  }

}
/**
 * In BDD tests ClientContext represents separated, connected user.
 */
@Accessors(fluent = true)
class ClientContext {
  @Getter
  ValName currentProject;
  @Getter
  EntityId latestTimeentryId;
  @Getter
  EntityId reservedCustomer;

  @Getter
  LocalDate todayAsDto = LocalDate.newBuilder().setMonth(1).setDay(1).setYear(2020).build();

  @Getter
  private final KnownFacts known = new KnownFacts();

  public ProjectId setProjectId(@NonNull ValName projectAlias) {
    currentProject = projectAlias;
    var projectId = ProjectId.anyNew();
    known.projects().put(projectAlias, projectId);
    return known.projects().get(projectAlias);
  }

  public void newTimeentry(EntityId id, LocalDate when) {
    var entry = new TimeentryContext(when);
    known.timeentries.put(id, entry);
    latestTimeentryId = id;
  }

  public ProjectId getProjectId(@NonNull ValName projectAlias) {
    return known.projects().get(projectAlias);
  }

  public ValEmail getOperatorId(@NonNull ValName operatorAlias) {
    var actual = known.users.get(operatorAlias);
    if (actual != null)
      return actual;
    var emailAsString = "user@" + UUID.randomUUID();
    var email = ValEmail.of(emailAsString);
    known.users().put(operatorAlias, email);
    return email;
  }

  @Getter
  @Accessors(fluent = true, chain = false)
  class KnownFacts {
    private final Map<ValName, ValEmail> users = new HashMap<>();
    private final Map<ValName, ProjectId> projects = new HashMap<>();
    private final Map<EntityId, TimeentryContext> timeentries = new HashMap<>();
    private final Map<ValName, Tuple2<EntityId, CustomerModel>> customers = new HashMap<>();
    // private final Map<ValName, ShardedId> operators = new HashMap<>();
  }

  public void on(AppEvent event) {
    new Build()
      .add(CustomerReservedEvent.class, this::on)
      .add(CustomerUpdatedEvent.class, this::on)
      .add(OperatorAssignedEvent.class, this::on)
      .use(event);
  }

  private void on(CustomerReservedEvent event) { 
    reservedCustomer = event.entityId();
  }

  private void on(CustomerUpdatedEvent event) { 
    var id = event.entityId();
    var model = event.cmd().getModel();
    var newId = event.newEntityId();

    known.customers.remove(event.customerAlias());
    known.customers.put(event.customerAlias(), Tuple.of(newId, model));
  }

  private void on(OperatorAssignedEvent event) {
    known.users().computeIfAbsent(currentProject, key -> ValEmail.of(currentProject + "-email"));
  }

  class Build implements EventConsumer {

    private final HashMap<Class<?>, Consumer<?>> handlers = new HashMap<>();

    @Override
    public <T extends AppEvent> EventConsumer add(Class<T> handlerClass, Consumer<T> handler) {
      handlers.put(handlerClass, handler);
      return this;
    }

    @Override
    public void use(AppEvent event) {
      var eventClass = event.getClass();
      var handlerKV = handlers.entrySet().stream().filter(it -> it.getKey().equals(eventClass)).findAny();
      if (handlerKV.isEmpty()) {
        throw new IllegalStateException();
      }
      var handler = (Consumer<AppEvent>) handlerKV.get().getValue(); 
      handler.accept(event);
    }

  }

}

sealed interface AppEvent { }

record CustomerReservedEvent(ValName customerAlias, EntityId entityId) implements AppEvent { }
record CustomerUpdatedEvent(ValName customerAlias, EntityId entityId, EntityId newEntityId, UpdateCommand cmd) implements AppEvent { }
record OperatorAssignedEvent(ValName operatorAlias, ValName projectAlias) implements AppEvent { }


interface EventConsumer {
  <T extends AppEvent> EventConsumer add(Class<T> handlerClass, Consumer<T> handler);
  void use(AppEvent event);
}

record TimeentryContext(LocalDate when) {
}
