package sinnet.infra.adapters.grpc;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import sinnet.domain.models.EntityId;
import sinnet.domain.models.ProjectId;
import sinnet.domain.models.UserToken;

@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface Map {

    static final Map apply = Mappers.getMapper(Map.class);

    sinnet.grpc.common.UserToken map(UserToken it);

    @Mapping(target = "entityId", source = "id")
    @Mapping(target = "entityVersion", source = "tag")
    sinnet.grpc.common.EntityId map(EntityId it);

      /** Fixme. */
    default sinnet.grpc.projects.generated.ProjectId toGrpc(ProjectId it) {
        var entityId = it.id();
        var entityTag = it.tag();
        return sinnet.grpc.projects.generated.ProjectId.newBuilder()
            .setEId(entityId.toString())
            .setETag(entityTag)
            .build();
    }


}
