package sinnet;

import io.vavr.concurrent.Future;
import sinnet.models.EntityId;

/** Factory designed to create implementations of factories able to create entity and process incoming commands. */
public abstract class EntityFactoryBase<TEntity extends EntityRoot<TState, TCommand>, TState, TCommand> {
    protected abstract TState loadContext(EntityId eid);
    protected abstract Future<Void> saveContext(TState state);
    public abstract EntityRoot<TState, TCommand> newEntity();
}

