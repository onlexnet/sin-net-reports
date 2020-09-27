package sinnet.project;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import sinnet.Db;
import sinnet.user.Serviceman;

@Entity
@Table(name = "project")
@Getter
@Setter
public class ProjectDbModel {

    @Id
    private UUID entityId;

    @Column(length = Db.Project.NAME_LENGTH)
    private String name;

    @OneToMany(mappedBy = "project")
    private List<Serviceman> serviceman;
}
