package sinnet.gql;

import sinnet.grpc.common.UserToken;
import sinnet.grpc.timeentries.GetReply;

public interface TimeentriesMapper extends Mapper {
    default ServiceModel toGql(GetReply dto, UserToken userToken) {
        var item = dto.getItem();
        if (item == null) return null;
        var it = new ServiceModel(); 
        it.setProjectId(item.getEntityId().getProjectId());
        it.setEntityId(item.getEntityId().getEntityId());
        it.setEntityVersion(item.getEntityId().getEntityVersion());
        it.setServicemanName(item.getServicemanName());
        it.setWhenProvided(toGql(item.getWhenProvided()));
        it.setDescription(item.getDescription());
        it.setDistance(item.getDistance());
        it.setDuration(item.getDuration());

        it.setCustomerId(item.getCustomerId());
        it.setUserToken(userToken);
        return it;
    }
}
