package sinnet.dbo;

import sinnet.model.ValEmail;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import sinnet.dbo.DboSession.TransactionToken;

/** Reads and delete all already available as inputs ... . */
public interface DboInput {

  Uni<Array<ProjectInput>> readAndDelete(Uni<TransactionToken> tokenFlow);

  /** Simple model of returned value. */
  record ProjectInput(String name, ValEmail owner) {
  }
}
