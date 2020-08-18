package sinnet;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Fixme. */
@Component
public class ServicesSearchResolver implements GraphQLResolver<Services> {

    /** FixMe. */
    static final Random RANDOM = new Random();

    /**
     * FixMe.
     *
     * @param ignored ignored
     * @param filter fixme.
     * @return fixme
     */
    public ServicesSearchResult search(final Services ignored,
                                     final ServicesFilter filter) {
        final int magic10 = 10;
        final int magic20 = 20;
        var count = RANDOM.nextInt(magic10) + magic20;
        var items = IntStream
            .range(1, count)
            .boxed()
            .map(it -> new ServiceModel(
                "some person",
                LocalDate.now().toString(),
                DummyData.randomCustomerName()))
            .collect(Collectors.toList());
        var distance = RANDOM.nextInt();
        return new ServicesSearchResult(items, distance);
    }

}

/** Fixme. */
@Data
@NoArgsConstructor
@AllArgsConstructor
class ServicesSearchResult {
    private List<ServiceModel> items;
    private int totalDistance;
}
