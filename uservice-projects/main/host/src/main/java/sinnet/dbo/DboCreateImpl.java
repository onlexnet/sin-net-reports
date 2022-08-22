package sinnet.dbo;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Validator;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.mutiny.Mutiny.Session;

import io.grpc.Status;
import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.val;
import sinnet.model.ProjectVid;

@ApplicationScoped
@RequiredArgsConstructor
class DboCreateImpl implements DboCreate {

  private final Mutiny.SessionFactory factory;
  private final Validator validator;
  
  @Override
  public Uni<CreateResult> create(CreateContent entry) {
    var requestedId = entry.getRequestedId();
    var initialEtag = 1L;
    var result = ProjectVid.of(requestedId, initialEtag);
    var emailAsString = entry.getEmailOfOwner().value();
    var newEntityTemplate = new ProjectDbo()
        .setEntityId(requestedId.value())
        .setVersion(initialEtag)
        .setName("-")
        .setEmailOfOwner(emailAsString);

    var violations = validator.validate(newEntityTemplate);
    if (!violations.isEmpty()) {
      var errorResult = new ValidationFailed("Validation error");
      return Uni.createFrom().item(errorResult);
    }
    
    return factory.withTransaction(
      (session, tx) -> // load to memory a managed instance so that update may be done with preloaded instance with given locking type
          // if it is new entity, lets check if creating it may increase number of free projects more that allowed
          // TODO: it is naive implementation as multiple threads in parallel may create projects above the limit
          
          guardLimits(3, session, newEntityTemplate)

          .call(session::persist)
          .map(ignoredAndReplaced -> result)
          .map(it -> (CreateResult) new Success(it)));

  }

  private Uni<ProjectDbo> guardLimits(long limit, Session session, ProjectDbo detachedEntity) {
    val query = "SELECT count(*) from ProjectDbo T where T.emailOfOwner=:emailOfOwner";
    var emailOfOwner = detachedEntity.getEmailOfOwner();
    return session.createQuery(query, Long.class)
      .setParameter("emailOfOwner", emailOfOwner)
      .getSingleResult()
      .map(Long::intValue)
      .flatMap(count -> count >= limit
        ? Uni.createFrom().failure(Status.RESOURCE_EXHAUSTED.withDescription("Too many projects").asException())
        : Uni.createFrom().item(detachedEntity));
  }

}
