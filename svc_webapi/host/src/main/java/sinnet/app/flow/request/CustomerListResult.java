package sinnet.app.flow.request;

import java.util.List;

import sinnet.domain.models.Customer;

public record CustomerListResult(List<Customer> customers) {
}
