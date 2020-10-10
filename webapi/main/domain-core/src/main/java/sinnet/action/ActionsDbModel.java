package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class ActionsDbModel {

    @Id
    private UUID entityId;

    private String description;
    private String servicemanEmail;

    private String customerName;
    private LocalDate date;
    private Duration duration;
    private Integer distance;
}
