package sinnet.read.dailyreport;

import org.springframework.stereotype.Service;

import sinnet.events.ServiceRegistered;

/**
 * Manages projection of services provided for clients.
 */
@Service
public class DailyReportProjector {

    /**
     * Include just created service in list of services.
     * @param evt event
     * */
    public void on(final ServiceRegistered evt) {
    }
}
