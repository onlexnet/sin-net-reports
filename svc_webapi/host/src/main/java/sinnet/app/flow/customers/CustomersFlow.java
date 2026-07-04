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
import sinnet.app.flow.request.CustomerRemoveResult;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerReserveResult;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.flow.request.CustomerUpdateResult;
import sinnet.app.ports.in.CustomersPortIn;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.domain.models.Customer;

@Component
@RequiredArgsConstructor
class CustomersFlow implements CustomersPortIn {

    private final CustomersPortOut customersOutPort;

    @Override
    public <T> List<T> customerList(String projectId, String requestorEmail, Function<Customer, T> mapper) {
        return customersOutPort.customerList(projectId, requestorEmail, mapper);
    }

    @Override
    public CustomerRemoveResult remove(CustomerRemoveCommand request) {
        return customersOutPort.remove(request.customerId(), request.requestor());
    }

    @Override
    public CustomerReserveResult reserve(CustomerReserveCommand cmd) {
        return customersOutPort.reserve(cmd);
    }

    @Override
    public CustomerUpdateResult update(CustomerUpdateCommand request) {
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
    public CustomerGetResult customerGet(String projectId, String requestorEmail, String customerId) {
        return customersOutPort.customerGet(projectId, requestorEmail, customerId);
    }
    
}
