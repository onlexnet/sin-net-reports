package sinnet.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerValue implements EntityValue<CustomerValue> {
    @Builder.Default
    private String operatorEmail = null;
    @Builder.Default
    private String billingModel = null;
    @Builder.Default
    private String supportStatus = null;
    @Builder.Default
    private Integer distance = null;
    @Builder.Default
    private Name customerName = Name.empty();
    @Builder.Default
    private Name customerCityName = Name.empty();
    @Builder.Default
    private String customerAddress = null;
}
