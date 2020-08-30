package sinnet;

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
  /** FixMe. */
  private LocalDate whenProvided;
  /** FixMe. */
  private String forWhatCustomer;
  /** Description of the provided service to be printed later as a position of invoice's attachment. */
  private String description;
}
