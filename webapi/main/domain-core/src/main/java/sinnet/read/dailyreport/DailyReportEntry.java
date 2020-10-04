package sinnet.read.dailyreport;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

/** FixMe. */
@NoArgsConstructor
@Data
@Table(Const.PROJECTION_NAME)
public class DailyReportEntry {

    /** Unique id of th record. */
    @Id
    private Long id;

    /** When the service has been provided. */
    private LocalDate when;

    private String description;
}
