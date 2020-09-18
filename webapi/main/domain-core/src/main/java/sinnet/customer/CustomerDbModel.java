package sinnet.customer;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "customer_db_model")
@Data
public class CustomerDbModel {
    private static final int MAX_NAME_LENGTH = 200;

    @Id
    private UUID entityId;

    @Column(length = MAX_NAME_LENGTH, nullable = false)
    private String name;
}
