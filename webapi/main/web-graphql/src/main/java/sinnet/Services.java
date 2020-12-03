package sinnet;

import java.util.UUID;

import lombok.Value;

/** Represents context of GQL operations related to Services. */
@Value
public final class Services {
    private UUID projectId;
}
