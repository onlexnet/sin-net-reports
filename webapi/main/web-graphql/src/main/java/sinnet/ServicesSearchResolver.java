package sinnet;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import io.vavr.collection.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.read.DailyReports;

/** Fixme. */
@Component
public class ServicesSearchResolver implements GraphQLResolver<Services> {

    @Autowired
    private QueryGateway queryGateway;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param filter fixme.
     * @return fixme
     */
    public CompletableFuture<ServicesSearchResult> search(final Services ignored,
                                                          final ServicesFilter filter) {

        return queryGateway
            .query(new DailyReports.Ask(), DailyReports.Reply.class)
            .thenApplyAsync(it -> {
                if (!(it instanceof DailyReports.Reply.Some)) return new ServicesSearchResult();
                var result = (DailyReports.Reply.Some) it;
                var items = Stream
                    .ofAll(result.getEntries())
                    .map(i -> new ServiceModel(
                        "a", LocalDate.now(), i.getWhat()))
                    .toJavaList();
                return new ServicesSearchResult(
                    items, 1
                );
            });
    }

}

/** Fixme. */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ServicesSearchResult {
    private List<ServiceModel> items = Collections.emptyList();
    private int totalDistance;
}
