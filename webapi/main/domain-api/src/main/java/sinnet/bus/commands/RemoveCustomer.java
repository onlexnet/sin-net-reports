package sinnet.bus.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import sinnet.models.EntityId;


public interface RemoveCustomer {

    @AllArgsConstructor
    @Value
    @Jacksonized
    @Builder(toBuilder = true)
    final class Command {

        /** Address used to send the command to it's consumer. */
        public static final String ADDRESS = "cmd.RemoveCustomer";

        private EntityId id;
    }

    @AllArgsConstructor
    @Value
    @Jacksonized
    @Builder(toBuilder = true)
    class Result {
        private Boolean value;
    }
}
