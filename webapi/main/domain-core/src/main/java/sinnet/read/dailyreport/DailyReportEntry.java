package sinnet.read.dailyreport;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/** FixMe. */
@NoArgsConstructor
@Data
@Entity
@Table(name = Const.PROJECTION_NAME)
public class DailyReportEntry {

    /** Unique id of th record. */
    @Id @GeneratedValue
    private Long id;

    /** When the service has been provided. */
    private LocalDate when;
}
