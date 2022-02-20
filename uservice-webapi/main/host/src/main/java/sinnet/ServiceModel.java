package sinnet;

import java.time.LocalDate;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class ServiceModel {
  private @NonNull @Id String projectId;
  private @NonNull @Id String entityId;
  private @NonNull int entityVersion;
  private CustomerEntity customer;
  private String servicemanName;
  private @NonNull LocalDate whenProvided;
  private String description;
  private Integer distance;
  private Integer duration;
}
