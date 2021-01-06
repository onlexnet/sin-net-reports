package sinnet.customer;

import io.vavr.concurrent.Future;
import lombok.Value;
import sinnet.EntityFactoryBase;
import sinnet.EntityRoot;
import sinnet.models.EntityId;

public interface CustomerEntity {

    final class Factory extends EntityFactoryBase<CustomerEntity.Root, State, Command> {

        @Override
        protected State loadContext(EntityId eid) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected Future<Void> saveContext(State state) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public EntityRoot<State, Command> newEntity() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    final class Root implements EntityRoot<State, Command> {

        @Override
        public void init(State initial) {
            // TODO Auto-generated method stub

        }

        @Override
        public void on(Command cmd) {
            // TODO Auto-generated method stub

        }
    }

    @Value
    class State {
        private EntityId entityId;
    }

    interface Command {
    }
}
