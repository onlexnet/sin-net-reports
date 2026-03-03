package sinnet.infra.adapters.grpc;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.UserToken;

@Mapper
public interface Map {
    
    static final Map apply = Mappers.getMapper(Map.class);
    
    sinnet.grpc.common.UserToken map(UserToken it);
}
