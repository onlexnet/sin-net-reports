package sinnet.input;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;

import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniSubscriber;
import io.smallrye.mutiny.subscription.UniSubscription;
import io.smallrye.mutiny.vertx.core.AbstractVerticle;
import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.dbo.DboCreate;
import sinnet.dbo.DboFacade;
import sinnet.dbo.DboInput;
import sinnet.dbo.DboUpdate;
import sinnet.model.ProjectVid;

@RequiredArgsConstructor
@Slf4j
class ProjectInputService extends AbstractVerticle {

  private final DboFacade dboFacade;


  @Override
  public Uni<Void> asyncStart() {
    var flow = dboFacade.withTransaction();
    var result = dboFacade.readAndDelete(flow)
      .call(it -> {
        var builder = Uni.join().<ProjectVid>builder();
        for (var item : it) {
          var createdItem = createProject(item);
          builder.add(createdItem);
        }
        return builder.joinAll().andCollectFailures();
      });
      result.subscribe().withSubscriber(new UniSubscriber<Object>() {

        @Override
        public void onSubscribe(UniSubscription subscription) {
          log.info("1");
        }

        @Override
        public void onItem(Object item) {
          log.info("2");
        }

        @Override
        public void onFailure(Throwable failure) {
          log.info("3");
        }
        
      });
    return result;
  }

  private Uni<ProjectVid> createProject(DboInput.ProjectInput entry) {
    var id = dboFacade.randomId();
    var name = entry.name();
    var emailOfOwner = entry.owner();
    var createEntry = new DboCreate.CreateContent(id, emailOfOwner);
    return dboFacade.create(createEntry)
        .flatMap(it -> {
          if (it instanceof DboCreate.Success x) {
            return Uni.createFrom().item(x.getVid());
          }
          if (it instanceof DboCreate.ValidationFailed x) {
            return Uni.createFrom().failure(new IllegalArgumentException(x.getReason()));
          }
          return Uni.createFrom().failure(new IllegalStateException("Unknonw exception"));
        })
        .flatMap(created -> {
          var vid = created;
          var content = new DboUpdate.UpdateCommandContent(name, emailOfOwner);
          return dboFacade.updateCommand(vid, content);
        });
  }

}
