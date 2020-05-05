package sinnet.read.dailyreport;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import lombok.Setter;
import sinnet.appevents.ServicesProjection;
import sinnet.events.NewServiceRegistered;
import sinnet.read.DailyReports;

/**
 * Manages projection of services provided for clients.
 */
@Service
public class DailyReportProjector {

    /** Projection storage. */
    @Autowired
    @Setter
    private DailyReportRepository repository;

    /** Used to inform about changes in projection. */
    @Autowired
    @Setter
    private ApplicationEventPublisher publisher;

    /**
     * Include just created service in list of services.
     * @param evt event
     * */
    @EventHandler
    public void on(final NewServiceRegistered evt) {
        var model = new DailyReportEntry();
        model.setWhen(evt.getWhen());
        repository.save(model);

        publisher.publishEvent(new ServicesProjection.Changed());
    }

    /**
     * Query handler. See {@link DailyReports}
     * @param ask see {@link DailyReports.Ask}
     * @return see {@link DailyReports.Reply}
     */
    @QueryHandler
    public DailyReports.Reply reply(final DailyReports.Ask ask) {
        var summary = new DailyReports.ServiceSummary();
        summary.setWhen(ask.getWhen());
        return DailyReports.Reply.Some.builder().entry(summary).build();
    }

}
