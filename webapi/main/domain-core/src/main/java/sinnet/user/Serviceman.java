package sinnet.user;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import sinnet.Db;
import sinnet.project.ProjectDbModel;

@Entity
@Table(name = "serviceman")
@Getter
@Setter
public class Serviceman {

    @Id
    private UUID entityId;

    @ManyToOne(optional = false)
    private ProjectDbModel project;

    @Column(length = Db.User.EMAIL_LENGTH)
    private String email;
}
