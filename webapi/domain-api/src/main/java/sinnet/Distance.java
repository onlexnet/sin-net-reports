package sinnet;

import lombok.Value;

/** Type to keep distance between geo points. Unit - kilometers */
@Value(staticConstructor = "of")
public class Distance {

    /**
     * Value of the distance.
     * Invariant: value >=0. */
    private int value;
}
