package sinnet.bus.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import sinnet.models.EntityId;


public interface RemoveCustomer {

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = RemoveCustomer.Command.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Command {

        /** Address used to send the command to it's consumer. */
        public static final String ADDRESS = "cmd.RemoveCustomer";

        private EntityId id;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = RemoveCustomer.Result.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    class Result {
        private Boolean value;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }
}
