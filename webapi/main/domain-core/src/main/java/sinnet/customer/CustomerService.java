package sinnet.customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vavr.collection.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import sinnet.TopLevelVerticle;
import sinnet.VertxHandlerTemplate;
import sinnet.bus.commands.ChangeCustomer;
import sinnet.bus.commands.RemoveCustomer;
import sinnet.bus.query.FindCustomer;
import sinnet.bus.query.FindCustomer.Reply;
import sinnet.bus.query.FindCustomers;
import sinnet.bus.query.FindCustomers.Ask;
import sinnet.bus.query.FindCustomers.CustomerData;
import sinnet.customer.CustomerRepository.CustomerModel;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
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
        vertx.eventBus().consumer(RemoveCustomer.Command.ADDRESS, new RemoveCustomerHandler());
        super.start(startPromise);
    }

    final class SaveCustomerHandler extends VertxHandlerTemplate<ChangeCustomer.Command, EntityId> {

        SaveCustomerHandler() {
            super(ChangeCustomer.Command.class);
        }

        @Override
        protected Future<EntityId> onRequest(ChangeCustomer.Command msg) {
            var eid = EntityId.of(msg.getId().getProjectId(), msg.getId().getId(), msg.getId().getVersion());
            var requestor = msg.getRequestor();
            var newValue = msg.getValue();
            var requestedSecrets = msg.getSecrets();
            var requestedSecretsEx = msg.getSecretsEx();
            var requestedContacts = msg.getContacts();
            return repository
                .get(eid)
                .flatMap(it -> {
                    var actualSecrets = it
                        .map(v -> v.getSecrets())
                        .getOrElse(new CustomerSecret[0]);
                    var newSecrets = CustomerService.merge(requestor,
                                                           LocalDate.now(),
                                                           requestedSecrets, actualSecrets);
                    var actualSecretsEx = it
                        .map(v -> v.getSecretsEx())
                        .getOrElse(new CustomerSecretEx[0]);
                    var newSecretsEx = CustomerService.merge(requestor,
                                                             LocalDate.now(),
                                                             requestedSecretsEx, actualSecretsEx);
                    var newContacts = Arrays.stream(requestedContacts)
                        .map(v -> CustomerContact
                            .builder()
                            .firstName(v.getFirstName())
                            .lastName(v.getLastName())
                            .phoneNo(v.getPhoneNo())
                            .email(v.getEmail())
                            .build())
                        .toArray(CustomerContact[]::new);

                    return repository
                        .write(eid, newValue, newSecrets, newSecretsEx, newContacts)
                        .map(v1 -> EntityId.of(v1.getProjectId(), v1.getId(), v1.getVersion()));
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
                .flatMap(it -> it.map(v -> new FindCustomer.Reply(v.getId().getId(), v.getId().getVersion(),
                                                                  v.getValue(),
                                                                  v.getSecrets(),
                                                                  v.getSecretsEx(),
                                                                  v.getContacts()))
                                 .map(v -> Future.succeededFuture(v))
                                 .getOrElse(Future.failedFuture(new Exception("No data"))));
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

    final class RemoveCustomerHandler extends VertxHandlerTemplate<RemoveCustomer.Command, RemoveCustomer.Result>
                                      implements RemoveCustomer {
        RemoveCustomerHandler() {
            super(RemoveCustomer.Command.class);
        }

        @Override
        protected Future<Result> onRequest(Command cmd) {
            var busId = cmd.getId();
            var entityId = EntityId.of(busId.getProjectId(), busId.getId(), busId.getVersion());
            return repository
                .remove(entityId)
                .map(it -> new Result(it));
        }
    }

    private static CustomerData map(CustomerModel item) {
        return CustomerData.builder()
            .projectId(item.getId().getProjectId())
            .entityId(item.getId().getId())
            .entityVersion(item.getId().getVersion())
            .value(item.getValue())
            .secrets(item.getSecrets())
            .secretsEx(item.getSecretsEx())
            .contacts(item.getContacts())
            .build();
    }

    /**
     * Combines set of requested secrets with existing secrets so that
     * defines new secrets, updated secrets and remove non used secrets.
     */
    public static  CustomerSecret[] merge(Email requestor, LocalDate when, ChangeCustomer.Secret[] requested, CustomerSecret[] actual) {

        // 1) Extract items are identical (location / username / password) so that can be marked as 'unchanged'
        // 2) The rest is considered as 'updated' or 'new'
        var result = new LinkedList<CustomerSecret>();
        var existing = new ArrayList<>(Arrays.asList(actual));
        var candidates = new ArrayList<>(Arrays.asList(requested));
        while (!candidates.isEmpty()) {
            var candidate = candidates.get(0);
            candidates.remove(0);

            // is untauched?
            var exactMatch = existing.stream()
                .filter(it -> Objects.equals(it.getLocation(), candidate.getLocation())
                           && Objects.equals(it.getUsername(), candidate.getUsername())
                           && Objects.equals(it.getPassword(), candidate.getPassword()))
                .findFirst();
            if (exactMatch.isPresent()) {
                var foundMatch = exactMatch.get();
                existing.remove(foundMatch);
                result.add(foundMatch);
                continue;
            }

            // is updated?
            var similarMatch = existing.stream()
                .filter(it -> Objects.equals(it.getLocation(), candidate.getLocation())
                           && Objects.equals(it.getUsername(), candidate.getUsername()))
                .findFirst();
            if (similarMatch.isPresent()) {
                var foundSimilar = similarMatch.get();
                existing.remove(foundSimilar);
                result.add(new CustomerSecret(candidate.getLocation(),
                               candidate.getUsername(),
                               candidate.getPassword(),
                               requestor,
                               when));
                continue;
            }

            // so, finally, is new
            result.add(new CustomerSecret(candidate.getLocation(),
                candidate.getUsername(),
                candidate.getPassword(),
                requestor,
                when));
        }

        return result.toArray(CustomerSecret[]::new);
    }

    public static  CustomerSecretEx[] merge(Email requestor, LocalDate when, ChangeCustomer.SecretEx[] requested, CustomerSecretEx[] actual) {

        // 1) Extract items are identical (location / username / password) so that can be marked as 'unchanged'
        // 2) The rest is considered as 'updated' or 'new'
        var result = new LinkedList<CustomerSecretEx>();
        var existing = new ArrayList<>(Arrays.asList(actual));
        var candidates = new ArrayList<>(Arrays.asList(requested));
        while (!candidates.isEmpty()) {
            var candidate = candidates.get(0);
            candidates.remove(0);

            // is untauched?
            var exactMatch = existing.stream()
                .filter(it -> Objects.equals(it.getLocation(), candidate.getLocation())
                           && Objects.equals(it.getEntityName(), candidate.getEntityName())
                           && Objects.equals(it.getEntityCode(), candidate.getEntityCode())
                           && Objects.equals(it.getUsername(), candidate.getUsername())
                           && Objects.equals(it.getPassword(), candidate.getPassword()))
                .findFirst();
            if (exactMatch.isPresent()) {
                var foundMatch = exactMatch.get();
                existing.remove(foundMatch);
                result.add(foundMatch);
                continue;
            }

            // is updated?
            var similarMatch = existing.stream()
                .filter(it -> Objects.equals(it.getLocation(), candidate.getLocation())
                           && Objects.equals(it.getEntityName(), candidate.getEntityName())
                           && Objects.equals(it.getEntityCode(), candidate.getEntityCode())
                           && Objects.equals(it.getUsername(), candidate.getUsername()))
                .findFirst();
            if (similarMatch.isPresent()) {
                var foundSimilar = similarMatch.get();
                existing.remove(foundSimilar);
                result.add(new CustomerSecretEx(candidate.getLocation(),
                               candidate.getUsername(),
                               candidate.getPassword(),
                               candidate.getEntityName(),
                               candidate.getEntityCode(),
                               requestor,
                               when));
                continue;
            }

            // so, finally, is new
            result.add(new CustomerSecretEx(candidate.getLocation(),
                candidate.getUsername(),
                candidate.getPassword(),
                candidate.getEntityName(),
                candidate.getEntityCode(),
                requestor,
                when));
        }

        return result.toArray(CustomerSecretEx[]::new);
    }

}
