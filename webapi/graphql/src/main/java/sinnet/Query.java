package sinnet;

import java.util.Collections;
import java.util.Random;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import org.springframework.stereotype.Component;

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
    public Iterable<ServiceModel> getServices(final ServicesFilter filter) {
        return Collections.emptyList();
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
