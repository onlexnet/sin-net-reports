package sinnet;

import java.util.UUID;

public interface Entity {
    UUID getEntityId();
    int getEntityVersion();
}
