package sinnet.bus.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import sinnet.models.CustomerValue;
import sinnet.models.Email;
import sinnet.models.EntityId;


public interface ChangeCustomerData {

  @AllArgsConstructor
  @Value
  @Jacksonized
  @Builder(toBuilder = true)
  final class Command {

    /** Address used to send the command to it's consumer. */
    public static final String ADDRESS = "cmd.ChangeCustomerData";

    /** TODO describe */
    private Email requestor;
    private EntityId id;
    private CustomerValue value;
    private Secret[] secrets;
    private SecretEx[] secretsEx;
    private Contact[] contacts;
  }

  @AllArgsConstructor
  @Value
  @Jacksonized
  @Builder(toBuilder = true)
  final class Secret {
    private String location;
    private String username;
    private String password;
  }

  @AllArgsConstructor
  @Value
  @Jacksonized
  @Builder(toBuilder = true)
  final class SecretEx {
    private String location;
    private String entityName;
    private String entityCode;
    private String username;
    private String password;
  }

  @AllArgsConstructor
  @Value
  @Jacksonized
  @Builder(toBuilder = true)
  final class Contact {
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String email;
  }
}
