package sinnet.rpc;

import java.util.OptionalInt;

/** Entry point for Rpc functionality. */
public interface RpcAdapter {

  OptionalInt getServerPort();
}
