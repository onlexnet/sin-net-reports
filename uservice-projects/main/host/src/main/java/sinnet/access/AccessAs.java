package sinnet.access;

import sinnet.grpc.projects.UserToken;

/** Calculates for given {@code UserToken} what are  */
interface AccessAs<T extends RoleContextSet> {
  T calculate(UserToken userToken);
}
