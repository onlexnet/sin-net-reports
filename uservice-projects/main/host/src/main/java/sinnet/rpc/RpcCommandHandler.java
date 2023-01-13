package sinnet.rpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.vavr.control.Either;

/**
 * @param <Q> - request type
 * @param <S> - response type
 */
public interface RpcCommandHandler<Q extends GeneratedMessageV3, S extends GeneratedMessageV3> {

  Either<Status, S> apply(Q cmd);

  default void command(Q request, StreamObserver<S> responseStream) {
    var result = this.apply(request);
    if (result.isRight()) {
      responseStream.onNext(result.get());
    }
    if (result.isLeft()) {
      responseStream.onError(result.getLeft().asException());
    }
    responseStream.onCompleted();
  }
}
