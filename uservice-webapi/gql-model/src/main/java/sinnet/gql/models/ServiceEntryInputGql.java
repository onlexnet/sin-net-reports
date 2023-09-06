package sinnet.gql.models;

import java.time.LocalDate;

import lombok.Data;

/** TBD. */
@Data
public class ServiceEntryInputGql {
  private String customerId;
  private String description;
  private Integer distance;
  private Integer duration;
  private String servicemanName;
  private LocalDate whenProvided;
}
