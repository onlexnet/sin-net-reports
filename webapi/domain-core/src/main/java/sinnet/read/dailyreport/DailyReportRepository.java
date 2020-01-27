package sinnet.read.dailyreport;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Projection of Daily reports. */
@Repository
public interface DailyReportRepository
       extends CrudRepository<DailyReportEntry, String> {
}
