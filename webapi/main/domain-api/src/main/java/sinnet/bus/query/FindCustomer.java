package sinnet.bus.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.bus.JsonMessage;
import sinnet.models.CustomerAuthorization;
import sinnet.models.CustomerValue;

public interface FindCustomer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Ask implements JsonMessage {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.FindCustomer";

        private UUID projectId;
        private UUID entityId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Reply implements JsonMessage {
        private UUID entityId;
        private int entityVersion;
        private CustomerValue value;
        private CustomerAuthorization[] authorizations;
    }
}
