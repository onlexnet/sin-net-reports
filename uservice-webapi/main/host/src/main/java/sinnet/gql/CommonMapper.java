package sinnet.gql;

import sinnet.Entity;
import sinnet.grpc.common.EntityId;

public interface CommonMapper {

    default Entity toGql(EntityId it) {
        if (it == null) return null;
        var result = new Entity();
        result.setProjectId(it.getProjectId());
        result.setEntityId(it.getEntityId());
        result.setEntityVersion(it.getEntityVersion());
        return result;
    }
}
