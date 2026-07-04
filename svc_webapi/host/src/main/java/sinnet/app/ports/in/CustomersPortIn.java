package sinnet.app.ports.in;

import java.util.List;
import java.util.function.Function;

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
import sinnet.domain.models.Customer;

public interface CustomersPortIn {

    CustomerGetResult customerGet(String projectId, String requestorEmail, String customerId);
    
    CustomerListResult list(CustomerListQuery query);
    
    CustomerGetResult get(CustomerGetQuery query);
    
    CustomerReserveResult reserve(CustomerReserveCommand projectId);

    <T> List<T> customerList(String projectId, String requestorEmail, Function<Customer, T> mapper);

    CustomerRemoveResult remove(CustomerRemoveCommand request);

    CustomerUpdateResult update(CustomerUpdateCommand request);

}