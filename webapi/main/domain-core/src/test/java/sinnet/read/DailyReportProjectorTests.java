package sinnet.read;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import sinnet.events.NewServiceActionRegistered;
import sinnet.read.dailyreport.DailyReportProjector;
import sinnet.read.dailyreport.DailyReportRepository;

/** Tests for {@link DailyReportProjector}. */
@DataJpaTest(showSql = true)
public class DailyReportProjectorTests {

    @Autowired
    private DailyReportRepository repository;

    /** Should produce report. */
    @Test
    public void shouldProduceReport() {

        var sut = new DailyReportProjector();
        sut.setPublisher(it -> {});
        sut.setRepository(repository);

        var now = LocalDate.now();

        var evt = new NewServiceActionRegistered();
        evt.setWhen(now);
        evt.setDescription("my description");

        sut.on(evt);

        var actual = sut.reply(new DailyReports.Ask(now));

        var expected = DailyReports.Reply.Some.builder()
            .entry(new DailyReports.ServiceSummary(now, "my description"))
            .build();

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
