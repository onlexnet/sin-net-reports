package sinnet.commands;

import java.util.UUID;

import lombok.Value;
import sinnet.models.Name;

@Value
public class RegisterNewCustomer implements VertxCommand {
    /** Address used to send the command to it's consumer. */
    public static final String ADDRESS = "cmd.RegisterNewCustomer";
    private UUID entityId;
    private Name customerName;
    private Name customerCityName;
    private String customerAddress;
}
