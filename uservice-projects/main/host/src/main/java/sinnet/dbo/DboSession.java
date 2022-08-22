package sinnet.dbo;

import io.smallrye.mutiny.Uni;

public interface DboSession {
  
  /** Starts a transaction operation on local storage. */
  Uni<TransactionToken> withTransaction();

  /** Shared token, used to share the same session / transaction context among multiple operations. */
  sealed interface TransactionToken permits DboTransactionToken {
  }

}
