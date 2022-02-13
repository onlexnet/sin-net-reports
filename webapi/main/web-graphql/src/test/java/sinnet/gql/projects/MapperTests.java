package sinnet.gql.projects;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import io.vavr.collection.Iterator;
import sinnet.grpc.projects.AvailableProject;
import sinnet.models.ProjectId;
import sinnet.read.ProjectProjector;

public class MapperTests {

    @Test
    public void shouldMapMinModel() {
        var model = new ProjectProjector.FindByServicemanModel(ProjectId.builder().build(), null);
        var msg = Mapper.map(Iterator.of(model)).toJavaList();
        assertThat(msg)
            .containsExactly(AvailableProject.newBuilder().build());
    }

    @Test
    public void shouldMapMaxModel() {
        var uuid = UUID.randomUUID();

        var model = new ProjectProjector.FindByServicemanModel(ProjectId.builder()
            .id(uuid)
            .version(1)
            .build(), "my project name");

        var msg = Mapper.map(Iterator.of(model)).toJavaList();

        assertThat(msg)
            .containsExactly(AvailableProject.newBuilder()
                .setId(uuid.toString())
                .setName("my project name")
                .build());
    }
}
