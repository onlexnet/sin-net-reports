package sinnet.dbo;

import org.hibernate.reactive.mutiny.Mutiny.Session;
import org.hibernate.reactive.mutiny.Mutiny.Transaction;

import lombok.Value;
import sinnet.dbo.DboSession.TransactionToken;

@Value(staticConstructor = "of")
class DboTransactionToken implements TransactionToken {
  private Session session;
  private Transaction transaction;
}
