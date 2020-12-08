package sinnet.customers;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
class CustomerEntry {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
@Builder
class CustomerModel {
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}
