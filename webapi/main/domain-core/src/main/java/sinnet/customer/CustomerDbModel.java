package sinnet.customer;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("customer_db_model")
@Data
public class CustomerDbModel {
    @Id
    private UUID entityId;

    private String name;
}
