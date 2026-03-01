package sinnet.app.ports.out;

import java.util.function.Function;

import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;

public interface CustomersOutPort {
    
    ListReply list(ListRequest request);
    
    <T> Iterable<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper);

}
