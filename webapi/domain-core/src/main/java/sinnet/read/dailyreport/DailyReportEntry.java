package sinnet.read.dailyreport;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

/** FixMe. */
@NoArgsConstructor
@Data
@Entity
public class DailyReportEntry {

    /** Unique id of th record. */
    @Id @GeneratedValue
    private Long id;

    /** When the service has been provided. */
    private LocalDate when;
}
