package sinnet.app.ports.in;

import java.util.List;
import java.util.function.Function;

import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.GetRequest;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.grpc.customers.UpdateResult;

public interface CustomersPortIn {
    
    CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId, Function<GetReply, CustomerEntityGql> mapper);
    
    ListReply list(ListRequest request);
    
    GetReply get(GetRequest request);
    
    ReserveReply reserve(ReserveRequest request);

    <T> List<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper);

    RemoveReply remove(RemoveRequest request);

    UpdateResult update(CustomerUpdateCommand request);

}