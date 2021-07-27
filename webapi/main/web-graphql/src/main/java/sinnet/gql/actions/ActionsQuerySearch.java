package sinnet.gql.actions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sinnet.read.ActionProjector;

/** Fixme. */
@Component
public class ActionsQuerySearch implements GraphQLResolver<ActionsQuery>,
                                           ActionProjector  {

    @Autowired
    private ActionProjector.Provider projection;

    /**
     * FixMe.
     *
     * @param gcontext ignored
     * @param filter fixme.
     * @return fixme
     */
    public CompletionStage<ServicesSearchResult> search(ActionsQuery gcontext,
                                                        ServicesFilter filter) {
        return projection
            .find(gcontext.getProjectId(), filter.getFrom(), filter.getTo())
            .map(it -> {
                var items = it
                    .map(item -> ServiceModel.builder()
                        .projectId(item.getEid().getProjectId())
                        .entityId(item.getEid().getId())
                        .servicemanName(item.getValue().getWho().getValue())
                        .whenProvided(item.getValue().getWhen())
                        .description(item.getValue().getWhat())
                        .duration(item.getValue().getHowLong().getValue())
                        .distance(item.getValue().getHowFar().getValue())
                        .localCustomerId(item.getValue().getWhom())
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
