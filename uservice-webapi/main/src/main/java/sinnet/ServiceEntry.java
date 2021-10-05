package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class ServiceEntry {
  private String servicemanName;
  private @NonNull LocalDate whenProvided;
  private @Id UUID customerId;
  private String description;
  private Integer distance;
  private Integer duration;
}
