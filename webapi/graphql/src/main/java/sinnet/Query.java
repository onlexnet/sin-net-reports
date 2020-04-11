package sinnet;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

/** FixMe. */
@Component
public class Query implements GraphQLQueryResolver {

    /** FixMe. */
    static final Random RANDOM = new Random();

    /**
     * FixMe.
     *
     * @param filter fixme.
     * @return fixme
     */
    public List<ServiceModel> getServices(final ServicesFilter filter) {
        final int magic10 = 10;
        final int magic20 = 10;
        var count = RANDOM.nextInt(magic10) + magic20;
        return IntStream
            .range(1, count)
            .boxed()
            .map(it -> new ServiceModel(
                LocalDate.now().toString(),
                DummyData.randomCustomerName()))
            .collect(Collectors.toList());
    }

}

/** FixMe. */
class DummyData {

    /** FixMe. */
    static final Random RANDOM = new Random();

    /**
     * FixMe.
     * @return TODO
     */
    public static String randomEmployeeName() {
        var candidates = "Anna Radek Bartek Mikołaj".split(" ");
        return candidates[RANDOM.nextInt(candidates.length)];
    };

    /**
     * FixMe.
     * @return TODO
     */
    public static String randomCustomerName() {
        var candidates = "Renoma Zgoda Medmax OrtMed".split(" ");
        return candidates[RANDOM.nextInt(candidates.length)];
    }

    /**
     * FixMe.
     * @return TODO
     */
    public static Integer randomDuration() {
        final int maxHoursPerService = 8;
        return RANDOM.nextInt(maxHoursPerService) + 1;
    }

    /**
     * FixMe.
     * @return TODO
     */
    public static Integer randomDistance() {
        final int maxDistance = 50;
        return RANDOM.nextInt(maxDistance);
    }

    /**
     * FixMe.
     * @return TODO
     */
    public String randomServiceDescription() {
        var candidates = ("Poprawa danych,Wprowadzanie korekt,"
                       + "Odtwarzanie danych,konfiguracja środowisk")
                       .split(",");
        return candidates[RANDOM.nextInt(candidates.length)];
    }
}
