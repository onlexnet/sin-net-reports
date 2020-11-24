package sinnet.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import sinnet.TopLevelVerticle;
import sinnet.commands.RegisterNewCustomer;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;

@Component
public class CustomerService extends AbstractVerticle implements TopLevelVerticle {

    private final CustomerRepository repository;

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        var address = RegisterNewCustomer.ADDRESS;
        vertx.eventBus().consumer(address, message -> {
            var eid = EntityId.some();
            repository.save(eid, CustomerValue.builder().build())
                .onComplete(it -> {
                    message.reply(message);
                });
        });

        super.start(startPromise);
    }
}
