package sinnet.gql;

import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import lombok.Value;

/** Allows to run given Behavior and send initial message. */
public interface SimpleMediatorRunner {
  <T> void spawn(Behavior<T> behaviorToSpawnAndRun, T initialMessage);
}

@Component
class SimpleMediatorRunnerImpl implements SimpleMediatorRunner, AutoCloseable {

  /** The only one purpose of the Actor System is to run Mediators. */
  private ActorSystem<CreateRequest<?>> actorSystem;

  SimpleMediatorRunnerImpl() {
    var userGuardianBeh = Behaviors.<CreateRequest<?>>setup(ctx -> {
        return Behaviors.receiveMessage(msg -> this.create(ctx, msg));
    });
    actorSystem = ActorSystem.create(userGuardianBeh, "mediators");
  }

  private Behavior<CreateRequest<?>> create(ActorContext<CreateRequest<?>> ctx, CreateRequest<?> msg) {
    var randomName = UUID.randomUUID().toString();
    var mediator = ctx.spawn(msg.behavior, randomName);
    msg.continuation.accept(mediator);
    return Behaviors.same();
  }

  @Override
  public void close() throws Exception {
    actorSystem.terminate();
  }

  @Value
  private class CreateRequest<T> {
    private Behavior<T> behavior;
    private Consumer<ActorRef<?>> continuation;
  }

  @Override
  public <T> void spawn(Behavior<T> behaviorToSpawn, T initialMessage) {
    Consumer<ActorRef<?>> kickStartBehavior = ar -> {
      @SuppressWarnings("unchecked")
      var typed = (ActorRef<T>) ar;
      typed.tell(initialMessage);
    };

    var request = new CreateRequest<>(behaviorToSpawn, kickStartBehavior);
    actorSystem.tell(request);
  }
}
