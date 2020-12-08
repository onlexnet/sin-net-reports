package sinnet.actions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.ActionRepository;

/** Fixme. */
@Component
public class ActionsQuerySearch implements GraphQLResolver<ActionsQuery> {

    @Autowired
    private ActionRepository repository;

    /**
     * FixMe.
     *
     * @param gcontext ignored
     * @param filter fixme.
     * @return fixme
     */
    public CompletionStage<ServicesSearchResult> search(ActionsQuery gcontext,
                                                        ServicesFilter filter) {
        return repository
            .find(gcontext.getProjectId(), filter.getFrom(), filter.getTo())
            .map(it -> {
                var items = it
                    .map(item -> ServiceModel.builder()
                                  .projectId(item.getProjectId())
                                  .entityId(item.getEntityId())
                                  .servicemanName(item.getValue().getWho().getValue())
                                  .whenProvided(item.getValue().getWhen())
                                  .forWhatCustomer(item.getValue().getWhom().getValue())
                                  .description(item.getValue().getWhat())
                                  .duration(item.getValue().getHowLong().getValue())
                                  .distance(item.getValue().getHowFar().getValue())
                                  .build())
                    .toJavaList();
                return new ServicesSearchResult(items, 2);
            })
            .toCompletionStage();
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
