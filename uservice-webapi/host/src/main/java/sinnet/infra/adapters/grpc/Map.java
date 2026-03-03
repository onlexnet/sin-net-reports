package sinnet.infra.adapters.grpc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.UserToken;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface Map {
    
    static final Map apply = Mappers.getMapper(Map.class);

    @Mapping(target = "mergeFrom", ignore = true)
    @Mapping(target = "clearField", ignore = true)
    @Mapping(target = "clearOneof", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "mergeUnknownFields", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "projectIdBytes", ignore = true)
    @Mapping(target = "requestorEmailBytes", ignore = true)
    sinnet.grpc.common.UserToken map(UserToken it);

    @Mapping(target = "mergeFrom", ignore = true)
    @Mapping(target = "clearField", ignore = true)
    @Mapping(target = "clearOneof", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "mergeUnknownFields", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "projectIdBytes", ignore = true)
    @Mapping(target = "entityIdBytes", ignore = true)
    @Mapping(target = "entityId", source = "id")
    @Mapping(target = "entityVersion", source = "tag")
    sinnet.grpc.common.EntityId map(EntityId it);
}
