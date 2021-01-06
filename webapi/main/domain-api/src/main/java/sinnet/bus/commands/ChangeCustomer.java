package sinnet.bus.commands;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;


public interface ChangeCustomer {

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.Command.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Command {

        /** Address used to send the command to it's consumer. */
        public static final String ADDRESS = "cmd.RegisterNewCustomer";

        private Email requestor;
        private EntityId id;
        private CustomerValue value;
        private Secret[] secrets;
        private SecretEx[] secretsEx;
        private Contact[] contacts;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.Secret.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Secret {
        private String location;
        private String username;
        private String password;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.SecretEx.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class SecretEx {
        private String location;
        private String entityName;
        private String entityCode;
        private String username;
        private String password;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }

    @AllArgsConstructor
    @Value
    @JsonDeserialize(builder = ChangeCustomer.Contact.MyBuilder.class)
    @Builder(builderClassName = "MyBuilder", toBuilder = true)
    final class Contact {
        private String firstName;
        private String lastName;
        private String phoneNo;
        private String email;

        @JsonPOJOBuilder(withPrefix = "")
        public static class MyBuilder {
        }
    }
}
