package sinnet.action;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
class ActionDbModel {

    @Id
    private UUID entityId;

    private String description;
    private String servicemanName;
    private String customerName;
}
