package sinnet.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Stream;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;
import sinnet.TopLevelVerticle;
import sinnet.VertxHandlerTemplate;
import sinnet.bus.commands.UpdateCustomerInfo;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomer.Reply;
import sinnet.bus.query.FindCustomers;
import sinnet.bus.query.FindCustomers.Ask;
import sinnet.bus.query.FindCustomers.CustomerData;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;

@Component
@Slf4j
public class CustomerService extends AbstractVerticle implements TopLevelVerticle {

    private final CustomerRepository repository;

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // TODO handle when registration of consumers is ready
        vertx.eventBus().consumer(UpdateCustomerInfo.ADDRESS, new RegisterNewCustomerHandler());
        vertx.eventBus().consumer(FindCustomer.Ask.ADDRESS, new FindCustomerHandler());
        vertx.eventBus().consumer(FindCustomers.Ask.ADDRESS, new FindCustomersHandler());
        super.start(startPromise);
    }

    final class RegisterNewCustomerHandler extends VertxHandlerTemplate<UpdateCustomerInfo, sinnet.bus.EntityId> {

        RegisterNewCustomerHandler() {
            super(UpdateCustomerInfo.class);
        }

        @Override
        protected Future<sinnet.bus.EntityId> onRequest(UpdateCustomerInfo msg) {
            var eid = EntityId.of(msg.getId().getProjectId(), msg.getId().getId(), msg.getId().getVersion());
            var value = msg.getValue();
            var authorizations = msg.getAuthorizations();
            return repository.save(eid, value, authorizations)
                    .map(it -> new sinnet.bus.EntityId(it.getProjectId(), it.getId(), it.getVersion()));
        }
    }

    final class FindCustomerHandler extends VertxHandlerTemplate<FindCustomer.Ask, FindCustomer.Reply> {
        FindCustomerHandler() {
            super(FindCustomer.Ask.class);
        }

        @Override
        protected Future<Reply> onRequest(sinnet.bus.query.FindCustomer.Ask request) {
            return repository.get(request.getProjectId(), request.getEntityId())
                .map(it -> new FindCustomer.Reply(it.getEntityId(), it.getVersion(), it.getValue()));
        }
    }

    final class FindCustomersHandler extends VertxHandlerTemplate<FindCustomers.Ask, FindCustomers.Reply> {
        FindCustomersHandler() {
            super(FindCustomers.Ask.class);
        }

        @Override
        protected Future<FindCustomers.Reply> onRequest(Ask request) {
            var projectId = request.getProjectId();
            return repository.list(projectId)
                .map(it -> Stream.ofAll(it).map(CustomerService::map).toJavaArray(CustomerData[]::new))
                .map(it -> new FindCustomers.Reply(it));
        }
    }

    private static CustomerData map(Entity<CustomerValue> item) {
        return CustomerData.builder()
            .projectId(item.getProjectId())
            .entityId(item.getEntityId())
            .entityVersion(item.getVersion())
            .value(item.getValue())
            .build();
    }
}
