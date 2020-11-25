package sinnet.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import sinnet.TopLevelVerticle;
import sinnet.commands.RegisterNewCustomer;
import sinnet.models.CustomerValue;
import sinnet.models.EntityId;
import sinnet.models.Name;

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
            var msg = JsonObject.mapFrom(message.body()).mapTo(RegisterNewCustomer.class);
            var eid = EntityId.some();
            var value = CustomerValue.builder()
                .customerName(Name.of(msg.getCustomerCityName()))
                .customerCityName(Name.of(msg.getCustomerCityName()))
                .customerAddress(msg.getCustomerCityName())
                .build();
            repository.save(eid, value)
                .onComplete(it -> {
                    var result = sinnet.bus.EntityId.of(eid).json();
                    message.reply(result);
                });
        });

        super.start(startPromise);
    }
}
