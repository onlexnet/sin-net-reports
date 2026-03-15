package sinnet.app.ports.out;

import java.util.List;
import java.util.function.Function;

import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerReserveResult;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.domain.models.Customer;
import sinnet.domain.models.UserToken;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.UpdateResult;

public interface CustomersPortOut {
    
    CustomerGetResult get(CustomerGetQuery query);
    
    CustomerReserveResult reserve(CustomerReserveCommand request);
    
    CustomerListResult list(CustomerListQuery query);
    
    <T> List<T> customerList(String projectId, String requestorEmail, Function<Customer, T> mapper);

    CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId, Function<sinnet.grpc.customers.GetReply, CustomerEntityGql> mapper);

    RemoveReply remove(sinnet.domain.models.EntityId customerId, UserToken requestor);

    UpdateResult update(CustomerUpdateCommand request);

}
