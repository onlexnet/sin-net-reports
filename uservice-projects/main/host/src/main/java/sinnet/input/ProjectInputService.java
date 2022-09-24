package sinnet.input;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.UniSubscriber;
import io.smallrye.mutiny.subscription.UniSubscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.dbo.DboCreate;
import sinnet.dbo.DboFacade;
import sinnet.dbo.DboInput;
import sinnet.dbo.DboUpdate;
import sinnet.model.ProjectVid;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
@ActivateRequestContext
final class ProjectInputService {

  private final DboFacade dboFacade;

  public Uni<Void> run() {
    var flow = dboFacade.withTransaction();
    return dboFacade.readAndDelete(flow)
      .call(it -> {
        var builder = Uni.join().<ProjectVid>builder();
        for (var item : it) {
          var createdItem = createProject(item);
          builder.add(createdItem);
        }
        return builder.joinAll().andCollectFailures();
      }).replaceWithVoid();
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
