package sinnet.project;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Table("project")
@Getter
@Setter
public class ProjectDbModel {

    @Id
    private UUID entityId;

    private String name;
}
