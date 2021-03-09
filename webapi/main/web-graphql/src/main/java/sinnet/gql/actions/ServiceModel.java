package sinnet.gql.actions;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import sinnet.gql.Entity;

/** FixMe. */
@Builder
@Getter
public class ServiceModel implements Entity {
    private UUID projectId;
    private UUID entityId;
    private int entityVersion;
    private String servicemanName;
    private LocalDate whenProvided;
    private String forWhatCustomer;
    private String description;
    private Integer duration;
    private Integer distance;
}
