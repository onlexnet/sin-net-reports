package sinnet.dbo;

import javax.enterprise.context.ApplicationScoped;

import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor
final class DboSessionImpl implements DboSession {

  private final Mutiny.SessionFactory factory;

  @Override
  public Uni<TransactionToken> withTransaction() {
    return factory.withTransaction((session, tx) -> {
      var ctx = DboTransactionToken.of(session, tx);
      return Uni.createFrom().item(ctx);
    });
  }
}
