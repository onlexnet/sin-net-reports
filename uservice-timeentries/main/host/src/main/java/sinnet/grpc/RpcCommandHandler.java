package sinnet.grpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;

public interface RpcCommandHandler<TREQUEST extends GeneratedMessageV3, TRESPONSE extends GeneratedMessageV3> {

  TRESPONSE apply(TREQUEST cmd);

  default void command(TREQUEST request, StreamObserver<TRESPONSE> responseStream) {
    var result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
