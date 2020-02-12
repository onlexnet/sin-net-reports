package sinnet.read.dailyreport;

import org.springframework.data.domain.Example;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sinnet.DailyReport;
import sinnet.RegisteredServices;
import sinnet.events.NewServiceRegistered;

/**
 * Manages projection of services provided for clients.
 */
@Service
public class DailyReportProjector {

    /** Projection storage. */
    @Autowired
    private DailyReportRepository repository;

    /**
     * Include just created service in list of services.
     * @param evt event
     * */
    @EventHandler
    public void on(final NewServiceRegistered evt) {
        var model = new DailyReportEntry();
        model.setWhen(evt.getWhen());
        repository.save(model);
    }

    /**
     * Query handler.
     * @param ask query
     * @return an answer
     */
    @QueryHandler
    public DailyReport.Reply reply(final DailyReport.Ask ask) {
        var summary = new DailyReport.ServiceSummary();
        summary.setWhen(ask.getWhen());
        return DailyReport.Reply.Some.builder().entry(summary).build();
    }

    /**
     * Query handler. See {@link RegisteredServices}
     * @param ask see {@link RegisteredServices.Ask}
     * @return see {@link RegisteredServices.Reply}
     */
    @QueryHandler
    public RegisteredServices.Reply reply(final RegisteredServices.Ask ask) {

        var probe = new DailyReportEntry();
        probe.setWhen(ask.getFilterToDay());
        var example = Example.of(probe);

        var items = repository.findAll(example).stream()
                    .map(it -> new RegisteredServices.ServiceEntry())
                    .toArray(RegisteredServices.ServiceEntry[]::new);

        var result = new RegisteredServices.Reply();
        result.setEntries(items);
        return result;
    }
}
