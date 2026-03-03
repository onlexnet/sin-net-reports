package sinnet.infra.adapters.grpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.UserToken;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface Map {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @Mapping(target = "mergeFrom", ignore = true)
    @Mapping(target = "clearField", ignore = true)
    @Mapping(target = "clearOneof", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    @Mapping(target = "mergeUnknownFields", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @interface IgnoreGrpcMetaFields {
    }
    
    static final Map apply = Mappers.getMapper(Map.class);

    @IgnoreGrpcMetaFields
    @Mapping(target = "projectIdBytes", ignore = true)
    @Mapping(target = "requestorEmailBytes", ignore = true)
    sinnet.grpc.common.UserToken map(UserToken it);

    @IgnoreGrpcMetaFields
    @Mapping(target = "projectIdBytes", ignore = true)
    @Mapping(target = "entityIdBytes", ignore = true)
    @Mapping(target = "entityId", source = "id")
    @Mapping(target = "entityVersion", source = "tag")
    sinnet.grpc.common.EntityId map(EntityId it);
}
