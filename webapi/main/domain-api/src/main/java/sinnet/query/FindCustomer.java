package sinnet.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.commands.VertxMessage;
import sinnet.models.CustomerValue;
import sinnet.models.Entity;

public interface FindCustomer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Ask implements VertxMessage {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.FindCustomer";

        private UUID entityId;
    }

    @Data
    class Reply {
        private Entity<CustomerValue> maybeEntity;
    }
}
