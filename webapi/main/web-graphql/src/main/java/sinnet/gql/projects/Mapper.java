package sinnet.gql.projects;

import javax.annotation.PostConstruct;

import org.modelmapper.ModelMapper;
import org.modelmapper.protobuf.ProtobufModule;
import org.springframework.stereotype.Component;

import io.vavr.collection.Iterator;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.projects.AvailableProject;
import sinnet.read.ProjectProjector;

public class Mapper {
    // private final ModelMapper mapper = new ModelMapper();
    // @PostConstruct
    // public void init() {
    //   mapper.registerModule(new ProtobufModule());
    //   mapper.registerModule(new ProtobufModule());
    // //   mapper
    // //     .typeMap(ProjectProjector.FindByServicemanModel.class, AvailableProject.Builder.class)
    // //     .addMappings(map -> map.map(src -> src.getId(), (dest, val) -> dest.setTargetField(Int64Value.newBuilder().setValue((Long) value).build())));
    // }

    public static  Iterator<AvailableProject> map(Iterable<ProjectProjector.FindByServicemanModel> template) {
        return Iterator.ofAll(template)
            .map(it -> PropsBuilder.build(AvailableProject.newBuilder())
              .tset(it.getId().getId().toString(), b -> b::setId)
              .tset(it.getName(), b -> b::setName)
              .done().build());
            
    }
}
