package sinnet.bus.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import sinnet.bus.EntityId;
import sinnet.bus.JsonMessage;
import sinnet.models.CustomerValue;
import sinnet.models.Email;


public interface ChangeCustomer {

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.Command.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Command implements JsonMessage {

        /** Address used to send the command to it's consumer. */
        public static final String ADDRESS = "cmd.RegisterNewCustomer";

        private Email requestor;
        private EntityId id;
        private CustomerValue value;
        private Authorization[] authorizations;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.Authorization.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Authorization {
        private String location;
        private String username;
        private String password;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }
}
