package sinnet.bus.commands;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.bus.JsonMessage;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterNewCustomer implements JsonMessage {
    /** Address used to send the command to it's consumer. */
    public static final String ADDRESS = "cmd.RegisterNewCustomer";

    private UUID projectId;
    private String customerName;
    private String customerCityName;
    private String customerAddress;
}
