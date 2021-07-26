package sinnet.gql;


import java.util.UUID;

public interface Identified {
  UUID getProjectId();
  UUID getEntityId();
  int getEntityVersion();
}
