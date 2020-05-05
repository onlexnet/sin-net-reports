package sinnet.read.dailyreport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Projection of Daily reports. */
@Repository
public interface DailyReportRepository
       extends JpaRepository<DailyReportEntry, String> {
}
