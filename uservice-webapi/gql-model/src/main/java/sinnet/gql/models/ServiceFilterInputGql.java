package sinnet.gql.models;

import java.time.LocalDate;

import lombok.Data;
import lombok.experimental.Accessors;

/** GQL model. */
@Data
@Accessors(chain = true)
public class ServiceFilterInputGql {
  private LocalDate from;
  private LocalDate to;
}
