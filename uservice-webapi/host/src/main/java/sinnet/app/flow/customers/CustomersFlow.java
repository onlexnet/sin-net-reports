package sinnet.app.flow.customers;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.flow.request.CustomerRemoveCommand;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.gql.models.CustomerEntityGql;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.ReserveRequest;
import sinnet.grpc.customers.UpdateResult;

@Component
@RequiredArgsConstructor
class CustomersFlow implements CustomersPortIn {

    private final CustomersPortOut customersOutPort;

    @Override
    public <T> List<T> customerList(String projectId, String requestorEmail, Function<CustomerModel, T> mapper) {
        return customersOutPort.customerList(projectId, requestorEmail, mapper);
    }

    @Override
    public RemoveReply remove(CustomerRemoveCommand request) {
        return customersOutPort.remove(request.customerId(), request.requestor());
    }

    @Override
    public ReserveReply reserve(CustomerReserveCommand cmd) {
        return customersOutPort.reserve(cmd);
    }

    @Override
    public UpdateResult update(CustomerUpdateCommand request) {
        return customersOutPort.update(request);
    }

    @Override
    public CustomerGetResult get(CustomerGetQuery query) {
        return customersOutPort.get(query);
    }

    @Override
    public CustomerListResult list(CustomerListQuery query) {
        return customersOutPort.list(query);
    }

    @Override
    public CustomerEntityGql customerGet(String projectId, String requestorEmail, String customerId,
            Function<sinnet.grpc.customers.GetReply, CustomerEntityGql> mapper) {
        return customersOutPort.customerGet(projectId, requestorEmail, customerId, mapper);
    }
    
}
