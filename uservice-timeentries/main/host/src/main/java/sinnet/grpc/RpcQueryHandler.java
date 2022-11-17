package sinnet.grpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;

public interface RpcQueryHandler<TREQUEST extends GeneratedMessageV3, TRESPONSE extends GeneratedMessageV3> {

  TRESPONSE apply(TREQUEST request);

  default void query(TREQUEST request, StreamObserver<TRESPONSE> responseStream) {
    var result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
