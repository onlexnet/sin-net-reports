package sinnet.app.flow.customers;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.CustomersInPort;
import sinnet.app.ports.out.CustomersOutPort;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.RemoveRequest;

@Component
@RequiredArgsConstructor
class CustomersFlow implements CustomersInPort {

    private final CustomersOutPort customersOutPort;
    @Override
    public <T> List<T> customerList(String projectId, String requestorEmail, Function<CustomerModel, T> mapper) {
        return customersOutPort.customerList(projectId, requestorEmail, mapper);
    }
    @Override
    public RemoveReply remove(RemoveRequest request) {
        return customersOutPort.remove(request);
    }
    
}
