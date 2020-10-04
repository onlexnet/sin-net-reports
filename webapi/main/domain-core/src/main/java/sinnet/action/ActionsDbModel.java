package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("actions")
public class ActionsDbModel {

    @Id
    private UUID entityId;

    private String description;
    private String servicemanName;

    private String customerName;
    private LocalDate date;
    private Duration duration;
    private Integer distance;
}
