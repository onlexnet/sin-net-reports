package sinnet.read;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "serviceman")
@Data
@Accessors(chain = true)
public class ServicemanDbo {
    @Id
    @Column(name = "entity_id")
    private UUID entityId;
    @Column(name = "email")
    private String email;
    @Column(name = "project_entity_id")
    private UUID projectId;
    @Column(name = "entity_version")
    private Integer entityVersion;
    @Column(name = "custom_name")
    private String customName;
}
