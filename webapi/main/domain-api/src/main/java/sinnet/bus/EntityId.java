package sinnet.bus;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityId implements JsonMessage {
    private UUID projectId;
    private UUID id;
    private int version;

    public static EntityId of(sinnet.models.EntityId other) {
        return new EntityId(other.getProjectId(),  other.getId(), other.getVersion());
    }
}
