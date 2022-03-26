package sinnet.gql.models;

import sinnet.gql.utils.PropsBuilder;
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

    default EntityId toGrpc(Entity it) {
        if (it == null) return null;
        return PropsBuilder.build(EntityId.newBuilder())
            .set(b -> b::setProjectId, it.getProjectId())
            .set(b -> b::setEntityId, it.getEntityId())
            .set(b -> b::setEntityVersion, it.getEntityVersion())
            .done().build();
    }

}
