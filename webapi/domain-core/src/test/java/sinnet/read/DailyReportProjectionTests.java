package sinnet.read;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;
import sinnet.AppTestContext;
import sinnet.read.DailyReports;
import sinnet.RegisterNewServiceAction;
import sinnet.events.NewServiceRegistered;
import sinnet.read.dailyreport.DailyReportProjector;
import sinnet.read.dailyreport.DailyReportRepository;

/** Tests for DailyReportProjection. */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class DailyReportProjectionTests {

    @Autowired
    private DailyReportRepository repository;
    
    /** Should produce report. */
    @Test
    public void shouldProduceReport() {

        var sut = new DailyReportProjector();
        sut.setPublisher(it -> {});
        sut.setRepository(repository);

        var now = LocalDate.now();

        var evt = new NewServiceRegistered();
        evt.setWhen(now);

        sut.on(evt);

        var actual = sut.reply(new DailyReports.Ask(now));

        var expected = DailyReports.Reply.Some.builder()
            .entry(new DailyReports.ServiceSummary(now))
            .build();
            
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
