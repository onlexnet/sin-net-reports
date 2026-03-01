package sinnet.app.ports.in;

import java.util.List;
import java.util.function.Function;

import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;

public interface CustomersInPort {
    
    <T> List<T> customerList(String projectId, String requestorEmail, Function<sinnet.grpc.customers.CustomerModel, T> mapper);

    RemoveReply remove(RemoveRequest request);

}
