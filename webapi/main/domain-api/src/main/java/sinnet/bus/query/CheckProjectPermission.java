package sinnet.bus.query;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

public interface CheckProjectPermission {

    @AllArgsConstructor
    @Value
    @Jacksonized
    @Builder(toBuilder = true)
    class Ask {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.CheckProjectPermission";

        private UUID projectId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Reply {
        private Boolean result;
    }

}
