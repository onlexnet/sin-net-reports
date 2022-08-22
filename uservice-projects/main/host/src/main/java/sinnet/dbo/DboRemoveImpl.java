package sinnet.dbo;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import sinnet.model.ValProjectId;

@ApplicationScoped
@RequiredArgsConstructor
class DboRemoveImpl implements DboRemove {

  private final Mutiny.SessionFactory factory;

  @Override
  public Uni<Void> remove(ValProjectId idHolder) {

    var eid = idHolder.value();

    return factory.withTransaction(
      (session, tx) -> session.find(ProjectDbo.class, eid)

        .flatMap(session::remove)
        .flatMap(it -> session.flush()));
  }
  
}
