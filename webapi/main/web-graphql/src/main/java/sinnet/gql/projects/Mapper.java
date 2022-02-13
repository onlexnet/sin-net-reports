package sinnet.gql.projects;

import static sinnet.grpc.PropsBuilder.ofNullable;

import io.vavr.collection.Iterator;
import lombok.experimental.UtilityClass;
import sinnet.grpc.PropsBuilder;
import sinnet.grpc.projects.AvailableProject;
import sinnet.read.ProjectProjector;

@UtilityClass
class Mapper {

    public static Iterator<AvailableProject> map(Iterable<ProjectProjector.FindByServicemanModel> template) {
        return Iterator.ofAll(template)
            .map(it -> PropsBuilder.build(AvailableProject.newBuilder())
              .tset(ofNullable(it.getId(), v -> v.getId(), v -> v.toString()) , b -> b::setId)
              .tset(ofNullable(it.getName()), b -> b::setName)
              .done().build());
    }
}

