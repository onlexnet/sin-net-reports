package sinnet.grpc.timeentries;

import java.time.LocalDate;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import sinnet.app.ports.timeentries.TimeEntryModelMapper;
import sinnet.domain.models.EntityId;
import sinnet.domain.models.TimeEntry;

public class TimeEntryModelMapperTest {
    
    @Test
    void mapMapMinimalProject() {

        var dto = TimeEntryModel.newBuilder().build();

        var actual = TimeEntryModelMapper.apply.fromDto(dto);

        var expected = new TimeEntry(null, "", "", 0, 0, "", "", null);
        Assertions.assertThat(actual).isEqualTo(expected);
        
    }


    @Test
    void mapMapMinimalProjectWithInvalidDate() {

        var dto = TimeEntryModel.newBuilder().setWhenProvided(sinnet.grpc.timeentries.LocalDate.newBuilder()).build();

        var actual = TimeEntryModelMapper.apply.fromDto(dto);

        var expected = new TimeEntry(null, "", "", 0, 0, "", "", null);
        Assertions.assertThat(actual).isEqualTo(expected);
        
    }

    @Test
    void mapFullModel() {

        var projectId = UUID.randomUUID();
        var entityId = UUID.randomUUID();
        var dto = TimeEntryModel.newBuilder()
            .setEntityId(sinnet.grpc.common.EntityId.newBuilder().setProjectId(projectId.toString()).setEntityId(entityId.toString()).setEntityVersion(1))
            .setCustomerId("customerId")
            .setDescription("description")
            .setDistance(2)
            .setDuration(3)
            .setServicemanEmail("se")
            .setServicemanName("sn")
            .setWhenProvided(sinnet.grpc.timeentries.LocalDate.newBuilder().setYear(2001).setMonth(2).setDay(3))
            .build();

        var actual = TimeEntryModelMapper.apply.fromDto(dto);

        var expected = new TimeEntry(new EntityId(projectId, entityId, 1), 
        "customerId", "description", 2, 3, "se", "sn", LocalDate.of(2001, 2, 3));
        Assertions.assertThat(actual).isEqualTo(expected);
        
    }
}
