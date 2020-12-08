package sinnet.customer;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.Stream;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import sinnet.TopLevelVerticle;
import sinnet.VertxHandlerTemplate;
import sinnet.bus.commands.RegisterNewCustomer;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomers;
import sinnet.bus.query.FindCustomers.Ask;
import sinnet.bus.query.FindCustomers.CustomerData;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;
import sinnet.models.EntityId;
import sinnet.models.Name;

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
        vertx.eventBus().consumer(RegisterNewCustomer.ADDRESS, this::registerNewCustomer);
        vertx.eventBus().consumer(FindCustomer.Ask.ADDRESS, this::findCustomer);
        vertx.eventBus().consumer(FindCustomers.Ask.ADDRESS, new FindCustomersHandler());
        super.start(startPromise);
    }

    private void registerNewCustomer(Message<JsonObject> message) {
        var msg = JsonObject.mapFrom(message.body()).mapTo(RegisterNewCustomer.class);
        var eid = EntityId.anyNew(msg.getProjectId());
        var value = CustomerValue.builder()
                                 .customerName(Name.of(msg.getCustomerName()))
                                 .customerCityName(Name.of(msg.getCustomerCityName()))
                                 .customerAddress(msg.getCustomerAddress())
                                 .build();
        repository.save(eid, value).onComplete(it -> {
            var result = sinnet.bus.EntityId.of(eid).json();
            message.reply(result);
        });
    }

    private void findCustomer(Message<JsonObject> message) {
        Optional.of(message.body()).map(JsonObject::mapFrom).map(it -> it.mapTo(FindCustomer.Ask.class))
                .ifPresent(it -> {
                    repository.get(it.getProjectId(), it.getEntityId()).onComplete(ar -> {
                        if (ar.succeeded()) {
                            var entity = ar.result();
                            var reply = new FindCustomer.Reply(entity.getEntityId(), entity.getVersion(),
                                    entity.getValue()).json();
                            message.reply(reply);
                        } else {
                            log.error("findCustomer error", ar.cause());
                        }
                    });
                });
    }

    class FindCustomersHandler extends VertxHandlerTemplate<FindCustomers.Ask, FindCustomers.Reply> {
        FindCustomersHandler() {
            super(FindCustomers.Ask.class);
        }

        @Override
        protected Future<FindCustomers.Reply> onRequest(Ask request) {
            return repository.list()
                .map(it -> Stream.ofAll(it).map(CustomerService::map).toJavaArray(CustomerData[]::new))
                .map(it -> new FindCustomers.Reply(it));
        }
    }

    private static CustomerData map(Entity<CustomerValue> item) {
        return CustomerData.builder()
            .entityId(item.getEntityId())
            .entityVersion(item.getVersion())
            .value(item.getValue())
            .build();
    }
}
