package sinnet.app.ports.in;

import sinnet.app.flow.request.UsersSearchQuery;
import sinnet.app.flow.request.UsersSearchResult;

/** DocMe. */
public interface UsersSearchPortIn {

  /** DoxMe. */
  UsersSearchResult search(UsersSearchQuery query);
}
