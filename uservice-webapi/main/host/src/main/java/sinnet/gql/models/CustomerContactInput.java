package sinnet.gql.models;

import org.eclipse.microprofile.graphql.Input;

import lombok.Data;

@Data
@Input("CustomerContactInput")
public class CustomerContactInput {
  private String firstName;
  private String lastName;
  private String phoneNo;
  private String email;
}
