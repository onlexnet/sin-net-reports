package sinnet.app.flow.request;

import java.util.List;

import sinnet.grpc.customers.CustomerModel;

public record CustomerListResult(List<CustomerModel> customers) {
}
