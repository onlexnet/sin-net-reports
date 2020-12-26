package sinnet.bus.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.bus.EntityId;
import sinnet.bus.JsonMessage;
import sinnet.models.CustomerAuthorization;
import sinnet.models.CustomerValue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomerInfo implements JsonMessage {
    /** Address used to send the command to it's consumer. */
    public static final String ADDRESS = "cmd.RegisterNewCustomer";

    private EntityId id;
    private CustomerValue value;
    private CustomerAuthorization[] authorizations;
}
