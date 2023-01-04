package sinnet.rpc;

import com.google.protobuf.GeneratedMessageV3;

import io.grpc.stub.StreamObserver;

public interface RpcQueryHandler<TTREQUEST extends GeneratedMessageV3, TRESPONSE extends GeneratedMessageV3> {

  TRESPONSE apply(TTREQUEST request);

  default void query(TTREQUEST request, StreamObserver<TRESPONSE> responseStream) {
    TRESPONSE result = this.apply(request);
    responseStream.onNext(result);
    responseStream.onCompleted();
  }
}
