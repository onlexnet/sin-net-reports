package sinnet.customer;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import sinnet.TopLevelVerticle;
import sinnet.VertxHandlerTemplate;
import sinnet.bus.commands.ChangeCustomer;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomer.Reply;
import sinnet.bus.query.FindCustomers;
import sinnet.bus.query.FindCustomers.Ask;
import sinnet.bus.query.FindCustomers.CustomerData;
import sinnet.customer.CustomerRepository.CustomerModel;
import sinnet.models.CustomerAuthorization;
import sinnet.models.Email;
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
        // TODO handle when registration of consumers is ready
        vertx.eventBus().consumer(ChangeCustomer.Command.ADDRESS, new SaveCustomerHandler());
        vertx.eventBus().consumer(FindCustomer.Ask.ADDRESS, new FindCustomerHandler());
        vertx.eventBus().consumer(FindCustomers.Ask.ADDRESS, new FindCustomersHandler());
        super.start(startPromise);
    }

    final class SaveCustomerHandler extends VertxHandlerTemplate<ChangeCustomer.Command, sinnet.bus.EntityId> {

        SaveCustomerHandler() {
            super(ChangeCustomer.Command.class);
        }

        @Override
        protected Future<sinnet.bus.EntityId> onRequest(ChangeCustomer.Command msg) {
            var eid = EntityId.of(msg.getId().getProjectId(), msg.getId().getId(), msg.getId().getVersion());
            var requestor = msg.getRequestor();
            var newValue = msg.getValue();
            var requestedAuthorisations = msg.getAuthorizations();
            return repository
                .get(eid)
                .flatMap(it -> {
                    var actualAuthorisations = it.getAuthorisations();
                    var newAuthorisations = CustomerService.merge(requestor,
                                                                  LocalDate.now(),
                                                                  requestedAuthorisations, actualAuthorisations);
                    return repository.write(eid, newValue, newAuthorisations)
                        .map(v -> new sinnet.bus.EntityId(v.getProjectId(), v.getId(), v.getVersion()));
                });
        }
    }

    final class FindCustomerHandler extends VertxHandlerTemplate<FindCustomer.Ask, FindCustomer.Reply> {
        FindCustomerHandler() {
            super(FindCustomer.Ask.class);
        }

        @Override
        protected Future<Reply> onRequest(FindCustomer.Ask request) {
            return repository.get(request.getProjectId(), request.getEntityId())
                .map(it -> new FindCustomer.Reply(it.getId().getId(), it.getId().getVersion(), it.getValue()));
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
                .map(it -> List.ofAll(it).map(CustomerService::map).toJavaArray(CustomerData[]::new))
                .map(it -> new FindCustomers.Reply(it));
        }
    }

    private static CustomerData map(CustomerModel item) {
        return CustomerData.builder()
            .projectId(item.getId().getProjectId())
            .entityId(item.getId().getId())
            .entityVersion(item.getId().getVersion())
            .value(item.getValue())
            .authorisations(item.getAuthorisations())
            .build();
    }

    public static  CustomerAuthorization[] merge(Email requestor, LocalDate when, ChangeCustomer.Authorization[] requested, CustomerAuthorization[] actual) {
        var requestedByLocation = Stream.ofAll(Arrays.stream(requested)).groupBy(it -> it.getLocation());
        return requestedByLocation.values()
            .flatMap(it -> it)
            .map(it -> new CustomerAuthorization(
                                   it.getLocation(),
                                   it.getUsername(),
                                   it.getPassword(),
                                   requestor,
                                   when
                               ))
            .toJavaArray(CustomerAuthorization[]::new);
    }
}
