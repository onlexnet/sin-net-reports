package sinnet.dbo;

import static sinnet.dbo.TransactionTokenExtensions.with;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.Transaction;

import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import lombok.RequiredArgsConstructor;
import sinnet.dbo.DboSession.TransactionToken;
import sinnet.model.ValEmail;

@ApplicationScoped
@RequiredArgsConstructor
class DboInputImpl implements DboInput {

  private final InputRepository repository;

  @Override
  public Uni<Array<ProjectInput>> readAndDelete(Uni<TransactionToken> tokenFlow) {
    return with(tokenFlow, this::readAndDelete);
  }

  private Uni<Array<ProjectInput>> readAndDelete(Session session, Transaction tx) {
    return repository.listAll()
    .map(Array::ofAll)
    .chain(this::delete)
    .map(it -> it.map(DboInputImpl::toResultModel));
  }

  private static ProjectInput toResultModel(ProjectInputDbo dbo) {
    var name = dbo.getName();
    var projectOwner = ValEmail.of(dbo.getEmailOfOwner());
    return new ProjectInput(name, projectOwner);
  }

  private Uni<Array<ProjectInputDbo>> delete(Array<ProjectInputDbo> items) {
    var builder = Uni.join().<Void>builder();
    for (var item : items) {
      var singleDelete = repository.delete(item);
      builder.add(singleDelete);
    }
    return builder.joinAll().andFailFast().map(ignored -> items);
  }


}
