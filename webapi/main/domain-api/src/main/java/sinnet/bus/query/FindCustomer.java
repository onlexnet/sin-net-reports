package sinnet.bus.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.bus.JsonMessage;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;

public interface FindCustomer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Ask implements JsonMessage {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.FindCustomer";

        private UUID entityId;
    }

    @Data
    class Reply {
        private Entity<CustomerValue> maybeEntity;
    }
}
