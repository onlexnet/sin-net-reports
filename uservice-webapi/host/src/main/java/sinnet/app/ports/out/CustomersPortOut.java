package sinnet.app.ports.out;

import java.util.List;
import java.util.function.Function;

import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerGetResult;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.flow.request.CustomerRemoveResult;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerReserveResult;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.flow.request.CustomerUpdateResult;
import sinnet.domain.models.Customer;
import sinnet.domain.models.UserToken;

public interface CustomersPortOut {

    CustomerGetResult get(CustomerGetQuery query);

    CustomerReserveResult reserve(CustomerReserveCommand request);

    CustomerListResult list(CustomerListQuery query);

    <T> List<T> customerList(String projectId, String requestorEmail, Function<Customer, T> mapper);

    CustomerGetResult customerGet(String projectId, String requestorEmail, String customerId);

    CustomerRemoveResult remove(sinnet.domain.models.EntityId customerId, UserToken requestor);

    CustomerUpdateResult update(CustomerUpdateCommand request);

}
