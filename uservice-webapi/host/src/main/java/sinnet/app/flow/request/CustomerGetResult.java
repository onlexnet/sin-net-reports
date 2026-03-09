package sinnet.app.flow.request;

import sinnet.grpc.customers.CustomerModel;

public record CustomerGetResult(CustomerModel customer) {
}
