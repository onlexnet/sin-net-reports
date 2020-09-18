package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "action_db_model")
@Data
class ActionDbModel {

    @Id
    private UUID entityId;

    private String description;
    private String servicemanName;
    private String customerName;
    private LocalDate when;
    private Duration duration;
    private Integer distance;
}
