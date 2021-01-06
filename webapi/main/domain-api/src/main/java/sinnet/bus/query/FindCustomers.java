package sinnet.bus.query;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.models.CustomerSecret;
import sinnet.models.CustomerSecretEx;
import sinnet.models.CustomerContact;
import sinnet.models.CustomerValue;

public interface FindCustomers {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Ask {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.FindCustomers";

        private UUID projectId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Reply {
        private CustomerData[] data;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    class CustomerData {
        private UUID projectId;
        private UUID entityId;
        private int entityVersion;
        private CustomerValue value;
        private CustomerSecret[] secrets;
        private CustomerSecretEx[] secretsEx;
        private CustomerContact[] contacts;
    }
}
