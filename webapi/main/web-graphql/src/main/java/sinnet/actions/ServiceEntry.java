package sinnet.actions;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** FixMe. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceEntry {
  /** Name of the person who provided the service. */
  private String servicemanName;
  private LocalDate whenProvided;
  private String forWhatCustomer;
  /** Description of the provided service to be printed later as a position of invoice's attachment. */
  private String description;
  private int distance;
  private int duration;
}
