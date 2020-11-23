package sinnet.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomerValue implements EntityValue<CustomerValue> {
    @Builder.Default
    private Name customerName = Name.empty();
    private Name customerCityName = Name.empty();
    private String customerAddress;
}
