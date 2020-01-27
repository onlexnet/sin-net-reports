package sinnet.read.dailyreport;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/** FixMe. */
@Data
@Entity
public class DailyReportEntry {

    /** Unique id of th record. */
    @Id
    private String id;
}
