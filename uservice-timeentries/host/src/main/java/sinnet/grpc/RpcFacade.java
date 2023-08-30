package sinnet.grpc;

import java.util.OptionalInt;

/** Entry point for Rpc functionality. */
public interface RpcFacade {

  OptionalInt getServerPort();
}
