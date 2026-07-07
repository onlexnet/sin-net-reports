package sinnet.grpc.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;
import lombok.Setter;

/**
 * TBD.
 */
public abstract class RpcCommandHandlerBase<Q extends GeneratedMessageV3, S extends GeneratedMessageV3> implements
          RpcCommandHandler<Q, S> {

  @Setter
  @Autowired
  private PlatformTransactionManager transactionManager;

  protected abstract S apply(Q cmd);

  /**
   * TBD.
   */
  public final void command(Q request, StreamObserver<S> responseStream) {
    var transactionTemplate = new TransactionTemplate(transactionManager);
    // start imperative jpa transaction
    transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    var cmdResult = transactionTemplate.execute(status -> {
      var result = this.apply(request);
      return result;
    });

    responseStream.onNext(cmdResult);
    responseStream.onCompleted();

  }

}
