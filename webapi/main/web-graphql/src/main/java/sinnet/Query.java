package sinnet;

import java.util.Random;

import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLQueryResolver;

/** FixMe. */
@Component
public class Query implements GraphQLQueryResolver {

    /**
     * Returns context of Services to allow provide more operations.
     * @return the context
     */
    public Services getServices() {
        return new Services();
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
