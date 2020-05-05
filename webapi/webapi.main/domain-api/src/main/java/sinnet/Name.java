package sinnet;

import lombok.Value;

/** Represents human-readable name of a company or a thing. */
@Value(staticConstructor = "of")
public class Name {
    /** Content of the Name. */
    private String value;
}
