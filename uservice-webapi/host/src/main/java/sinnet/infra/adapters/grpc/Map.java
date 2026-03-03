package sinnet.infra.adapters.grpc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.UserToken;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Map {
    
    static final Map apply = Mappers.getMapper(Map.class);
    
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "requestorEmail", target = "requestorEmail")
    sinnet.grpc.common.UserToken map(UserToken it);
}
