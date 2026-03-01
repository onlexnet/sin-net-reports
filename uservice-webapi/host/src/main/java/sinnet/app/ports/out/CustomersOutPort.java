package sinnet.app.ports.out;

import java.util.List;
import java.util.function.Function;

import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;

public interface CustomersOutPort {
    
    ListReply list(ListRequest request);
    
    <T> List<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper);

    CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId, Function<GetReply, CustomerEntityGql> mapper);

    RemoveReply remove(RemoveRequest request);

}
