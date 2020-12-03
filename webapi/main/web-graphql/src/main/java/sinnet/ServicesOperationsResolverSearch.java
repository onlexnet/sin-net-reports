package sinnet;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fixme. */
@Component
public class ServicesOperationsResolverSearch implements GraphQLResolver<ServicesOperations> {

    @Autowired
    private ActionRepository repository;

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param filter fixme.
     * @return fixme
     */
    public CompletionStage<ServicesSearchResult> search(ServicesOperations ignored,
                                                        ServicesFilter filter) {
        return repository
            .find(filter.getFrom(), filter.getTo())
            .map(it -> {
                var items = it
                    .map(item -> ServiceModel.builder()
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
