package sinnet.customers;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.collection.List;
import sinnet.AskTemplate;
import sinnet.SomeEntity;
import sinnet.bus.query.FindCustomers;

@Component
class CustomersQueryList extends AskTemplate<FindCustomers.Ask, FindCustomers.Reply>
                         implements GraphQLResolver<CustomersQuery> {

    CustomersQueryList() {
        super(FindCustomers.Ask.ADDRESS, FindCustomers.Reply.class);
    }

    CompletableFuture<List<CustomerEntity>> list(CustomersQuery gcontext) {
        var ask = new FindCustomers.Ask(gcontext.getProjectId());
        return super.ask(ask)
            .thenApply(it -> List.of(it.getData())
                                 .map(o -> {
                                     var id = new SomeEntity(o.getProjectId(), o.getEntityId(), o.getEntityVersion());
                                     var value = o.getValue();
                                     var auths = o.getAuthorisations();
                                     return new CustomerEntity(id, value, auths);
                                 }));
    }
}
