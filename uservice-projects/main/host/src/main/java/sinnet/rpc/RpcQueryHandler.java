package sinnet.rpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;

/**
 * @param <Q> - query type
 * @param <R> - response type
 */
public interface RpcQueryHandler<Q extends GeneratedMessageV3, R extends GeneratedMessageV3> {

  R apply(Q request);

  default void query(Q request, StreamObserver<R> responseStream) {
    R result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
