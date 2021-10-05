package sinnet;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ServiceFilter {
  private LocalDate from;
  private LocalDate to;
}
