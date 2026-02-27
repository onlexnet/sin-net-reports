package sinnet.app.ports.out;

/** DoxMe. */
public interface UsersServicePortOut {
    
  /** DoxMe. */
  sinnet.grpc.users.SearchReply search(sinnet.grpc.users.SearchRequest request);
}
