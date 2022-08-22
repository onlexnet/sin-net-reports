package sinnet.dbo;

import java.util.function.BiFunction;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.Transaction;

import io.smallrye.mutiny.Uni;
import lombok.experimental.UtilityClass;
import sinnet.dbo.DboSession.TransactionToken;

@UtilityClass
class TransactionTokenExtensions {

  /** Simple repeatable method to cast and extract session and transaction from the context of the operation.  */
  static <T> Uni<T> with(Uni<TransactionToken> tokenFlow, BiFunction<Session, Transaction, Uni<T>> flow) {
    return tokenFlow.flatMap(it -> {
      var token = (DboTransactionToken) it;
      var session = token.getSession();
      var transaction = token.getTransaction();
      return flow.apply(session, transaction);
    });
  }
}
