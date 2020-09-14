package sinnet;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Builder;

/** FixMe. */
@Builder
public class ServiceModel {
    private UUID entityId;
    private String servicemanName;
    private LocalDate whenProvided;
    private String forWhatCustomer;
    private String description;
    private Integer duration;
    private Integer distance;
}
