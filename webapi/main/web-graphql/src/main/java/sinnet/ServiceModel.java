package sinnet;

import java.time.LocalDate;

import lombok.Builder;

/** FixMe. */
@Builder
public class ServiceModel {
    private String servicemanName;
    private LocalDate whenProvided;
    private String forWhatCustomer;
    private String description;
    private Integer duration;
    private Integer distance;
}
