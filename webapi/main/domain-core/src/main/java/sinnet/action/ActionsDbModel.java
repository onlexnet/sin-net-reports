package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ActionsDbModel {

    private UUID entityId;

    private String description;
    private String servicemanEmail;

    private String customerName;
    private LocalDate date;
    private Duration duration;
    private Integer distance;
}
