package sinnet.rpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;

/**
 * @param <Q> - query type
 * @param <S> - response type
 */
public interface RpcQueryHandler<Q extends GeneratedMessageV3, S extends GeneratedMessageV3> {

  S apply(Q request);

  default void query(Q request, StreamObserver<S> responseStream) {
    S result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
