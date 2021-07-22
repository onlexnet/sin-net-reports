package sinnet.action;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import sinnet.ActionRepository;
import sinnet.Api;
import sinnet.AppTestContext;
import sinnet.Dates;
import sinnet.Sync;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.EntityId;
import sinnet.read.ActionProjector;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ContextConfiguration(classes = AppTestContext.class)
@TestPropertySource(value = {
    "/domain-core.properties"
})
public class ActionRepositoryTests {

  @Autowired
  private Api api;

  @Autowired
  private ActionRepository sut;

  @Autowired
  private ActionProjector.Provider projection;

  @Test
  public void myTest() {
    var projectEid = api.createNewProject();
    var customerId = api.defineCustomer(EntityId.anyNew(projectEid));
    var projectId = projectEid.getId();

    var now = Dates.gen().head();
    Sync.of(() -> projection.find(projectId, now, now));

    Sync.of(() -> projection.find(projectId, now, now))
        .checkpoint(actual -> Assumptions.assumeThat(actual).isEmpty());

      var newEntity = ActionValue.builder()
          .howFar(Distance.of(10))
          .when(now)
          .whom(customerId.getId())
          .build();

      Sync.of(() -> sut.save(EntityId.anyNew(projectId), newEntity))
          .and(it -> sut.save(EntityId.anyNew(projectId), newEntity))
          .and(it -> sut.save(EntityId.anyNew(projectId), newEntity))
          .and(it -> projection.find(projectId, now, now))
          .checkpoint(actualItems -> {
              Assertions.assertThat(actualItems).hasSize(3);
              var expected = ActionValue.builder()
                  .howFar(Distance.of(10))
                  .when(now)
                  .whom(customerId.getId())
                  .build();
              var actual = actualItems.head();
              Assertions.assertThat(actual.getValue()).isEqualTo(expected);
          });
  }

  @Test
  public void shouldSaveFullModel() {
    var projectEid = api.createNewProject();
    var customerId = api.defineCustomer(EntityId.anyNew(projectEid));
    var projectId = projectEid.getId();

    var now = Dates.gen().head();
    var newEntity = ActionValue.builder()
        .who(Email.of("some person"))
        .howFar(Distance.of(1))
        .howLong(ActionDuration.of(2))
        .what("some action")
        .when(now)
        .whom(customerId.getId())
        .build();

    Sync
        .of(() -> sut.save(EntityId.anyNew(projectId), newEntity))
        .checkpoint(actual -> assertThat(actual).isTrue())
        .and(it -> projection.find(projectId, now, now))
        .checkpoint(actual -> {
            var expected = ActionValue.builder()
                .who(Email.of("some person"))
                .howFar(Distance.of(1))
                .howLong(ActionDuration.of(2))
                .what("some action")
                .when(now)
                .whom(customerId.getId())
                .build();
            assertThat(actual.head().getValue()).isEqualTo(expected);
        });
  }

    @Test
    public void shouldUpdateFullModel() {
        var now = Dates.gen().head();
        var projectEid = api.createNewProject();
        var customerId = api.defineCustomer(EntityId.anyNew(projectEid));
        var projectId = projectEid.getId();
    
        var newEntity = ActionValue.builder()
            .when(Dates.gen().head())
            .build();
        var updateEntity = ActionValue.builder()
            .who(Email.of("some person"))
            .howFar(Distance.of(1))
            .howLong(ActionDuration.of(2))
            .what("some action")
            .when(now)
            .whom(customerId.getId())
            .build();

        var entityId = EntityId.anyNew(projectId);
        Sync
            .of(() -> sut.save(entityId, newEntity))
            .checkpoint(it -> assertThat(it).isTrue())
            .and(it -> sut.update(entityId, updateEntity))
            .and(it -> projection.find(projectId, now, now))
            .checkpoint(actual -> {
                var expected = ActionValue.builder()
                    .who(Email.of("some person"))
                    .howFar(Distance.of(1))
                    .howLong(ActionDuration.of(2))
                    .what("some action")
                    .when(now)
                    .whom(customerId.getId())
                    .build();
                assertThat(actual.head().getValue()).isEqualTo(expected);
            });
    }

    @Test
    public void shouldSaveMinModel() {
        var now = Dates.gen().head();
        var projectEid = api.createNewProject();
        var projectId = projectEid.getId();
        
        var newEntity = ActionValue.builder()
            .when(now)
            .build();

        var entityId = EntityId.anyNew(projectId);
        Sync
            .of(() -> sut.save(entityId, newEntity))
            .checkpoint(it -> assertThat(it).isTrue())
            .and(it -> sut.find(projectId, entityId.getId()))
            .checkpoint(actual -> {
                var expected = ActionValue.builder()
                .when(now)
                .build();
                assertThat(actual.getValue()).isEqualTo(expected);
            });

    }
}
