package sinnet.read;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import sinnet.AppTestContext;
import sinnet.events.ServiceRegistered;
import sinnet.read.dailyreport.DailyReportProjector;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AppTestContext.class })
public final class DailyReportProjectionTests {

    @Autowired
    DailyReportProjector projector;
    
    @Test
    public void shouldProduceReport() {

        projector.on(new ServiceRegistered());

    }
}