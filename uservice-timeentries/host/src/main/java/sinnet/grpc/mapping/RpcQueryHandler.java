package sinnet.grpc.mapping;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

/**
 * Generic Query handler.
 */
@Transactional(TxType.REQUIRES_NEW)
public interface RpcQueryHandler<Q extends GeneratedMessageV3, S extends GeneratedMessageV3> {

  /**
   * TBD.
   */
  S apply(Q request);

  /**
   * TBD.
   */
  default void query(Q request, StreamObserver<S> responseStream) {
    S result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
