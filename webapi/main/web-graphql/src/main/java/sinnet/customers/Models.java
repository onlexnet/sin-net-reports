package sinnet.customers;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

@Data
class CustomerEntry {
    private String operatorEmail;
    private String billingModel;
    private String serviceStatus;
    private Integer distance;
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}

@Value
@Builder
class CustomerModel {
    private String operatorEmail;
    private String billingModel;
    private String serviceStatus;
    private Integer distance;
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}
