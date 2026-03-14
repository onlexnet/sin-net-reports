package sinnet.app.ports.in;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.flow.request.CustomerRemoveCommand;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.UpdateResult;

public interface CustomersPortIn {
    
    CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId,
            Function<sinnet.grpc.customers.GetReply, CustomerEntityGql> mapper);
    
    CustomerListResult list(CustomerListQuery query);
    
    CustomerGetResult get(CustomerGetQuery query);
    
    ReserveReply reserve(CustomerReserveCommand projectId);

    <T> List<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper);

    RemoveReply remove(CustomerRemoveCommand request);

    UpdateResult update(CustomerUpdateCommand request);

}