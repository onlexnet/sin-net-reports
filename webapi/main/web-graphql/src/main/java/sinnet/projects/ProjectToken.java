package sinnet.projects;

import java.util.UUID;

import lombok.Value;

@Value
public class ProjectToken {
    private UUID projectId;
    private String checkCode;
}
