package sinnet;

import lombok.Value;

/**
 * Name of en Entity allows to recognize the entity.
 */
@Value(staticConstructor = "of")
public class Name {
    /** FixMe. */
    private String value;
}
