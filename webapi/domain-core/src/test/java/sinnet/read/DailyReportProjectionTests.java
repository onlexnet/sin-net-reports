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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.SneakyThrows;
import sinnet.AppTestContext;
import sinnet.DailyReport;
import sinnet.RegisterNewServiceActionCommand;

/** Tests for DailyReportProjection. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppTestContext.class })
@EnableAutoConfiguration
public class DailyReportProjectionTests {

    /** Command Gateway. */
    @Autowired
    private CommandGateway commandGateway;

    /** Query Gateway. */
    @Autowired
    private QueryGateway queryGateway;

    /** Should produce report. */
    @Test
    @SneakyThrows
    public void shouldProduceReport() {
        var now = LocalDate.of(2001, 2, 3);
        var cmd = RegisterNewServiceActionCommand.builder().when(now);
        commandGateway.send(cmd);

        var actual = queryGateway
            .query(new DailyReport.Ask(now), DailyReport.Reply.class)
            .get(300, TimeUnit.MILLISECONDS);

        var expected = DailyReport.Reply.Some.builder()
            .entry(new DailyReport.ServiceSummary(now))
            .build();
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
