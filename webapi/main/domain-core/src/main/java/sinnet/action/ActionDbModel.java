package sinnet.action;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import sinnet.Db;

@Entity
@Table(name = "action_db_model")
@Data
public class ActionDbModel {

    @Id
    private UUID entityId;

    private String description;
    private String servicemanName;

    @Column(name = "customer-name", nullable = false, length = Db.Customer.NAME_LENGTH)
    private String customerName;
    private LocalDate when;
    private Duration duration;
    private Integer distance;
}
