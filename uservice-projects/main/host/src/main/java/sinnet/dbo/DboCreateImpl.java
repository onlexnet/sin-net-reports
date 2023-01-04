package sinnet.dbo;

import org.springframework.stereotype.Component;

import io.grpc.Status;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import sinnet.model.ProjectVid;

@Component
@RequiredArgsConstructor
class DboCreateImpl implements DboCreate {

  private final Validator validator;
  private final ProjectRepository repository;

  @Override
  public CreateResult create(CreateContent entry) {
    var requestedId = entry.getRequestedId();
    var initialEtag = 1L; //well-known ID set by hibernate locking mechanism
    var result = ProjectVid.of(requestedId, initialEtag);
    var emailAsString = entry.getEmailOfOwner().value();
    var newEntityTemplate = new ProjectDbo()
        .setEntityId(requestedId.value())
        .setVersion(initialEtag)
        .setName("-")
        .setEmailOfOwner(emailAsString);


    var violations = validator.validate(newEntityTemplate);
    if (!violations.isEmpty()) {
      return new ValidationFailed("Validation error");
    }

    // TODO: it is naive implementation as multiple threads in parallel may create projects above the limit
    var emailOfOwner = newEntityTemplate.getEmailOfOwner();
    var count = repository.countByEmailOfOwner(emailOfOwner);
    val limit = 10;
    if (count >= limit) {
      var errorDesc = String.format("Too many projects: Current: %s, Limit:%s, user: %s", count, limit, emailOfOwner);
      return new ValidationFailed(errorDesc);
    }

    repository.save(newEntityTemplate);
    return new Success(result);

  }

}

