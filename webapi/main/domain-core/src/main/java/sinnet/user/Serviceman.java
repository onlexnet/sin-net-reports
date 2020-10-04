package sinnet.user;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("serviceman")
@Getter
@Setter
public class Serviceman {

    @Id
    private UUID entityId;

    private String email;
}
