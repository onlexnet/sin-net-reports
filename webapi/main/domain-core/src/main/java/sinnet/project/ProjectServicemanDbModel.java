package sinnet.project;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import sinnet.user.User;

@Entity
@Table(name = "project_serviceman")
@Getter
@Setter
public class ProjectServicemanDbModel {
    @Id
    private UUID id;

    private ProjectDbModel project;

    private User serviceman;
}
