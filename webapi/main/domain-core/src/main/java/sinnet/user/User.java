package sinnet.user;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import sinnet.Db;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    private UUID entityId;

    @Column(length = Db.User.EMAIL_LENGTH)
    private String email;
}
