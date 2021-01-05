package sinnet.bus.query;

import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

public interface CheckProjectPermission {

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = CheckProjectPermission.Ask.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    class Ask {
        /** Address used to send the query to it's handler. */
        public static final String ADDRESS = "query.CheckProjectPermission";

        private UUID projectId;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Reply {
        private Boolean result;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

}
