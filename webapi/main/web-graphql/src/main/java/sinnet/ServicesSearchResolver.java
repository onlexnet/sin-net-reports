package sinnet;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fixme. */
@Component
public class ServicesSearchResolver implements GraphQLResolver<Services> {

    @Autowired
    private ActionService actionService;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param filter fixme.
     * @return fixme
     */
    public ServicesSearchResult search(final Services ignored,
                                       final ServicesFilter filter) {
        var items = actionService
            .find(filter.getFrom(), filter.getTo())
            .block()
            .map(it -> ServiceModel.builder()
                .entityId(it.getEntityId())
                .servicemanName(it.getValue().getWho().getValue())
                .whenProvided(it.getValue().getWhen())
                .forWhatCustomer(it.getValue().getWhom().getValue())
                .description(it.getValue().getWhat())
                .duration((int) it.getValue().getHowLong().toMinutes())
                .distance(it.getValue().getHowFar().getValue())
                .build())
            .collect(Collectors.toList());
        return new ServicesSearchResult(items, 2);
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
