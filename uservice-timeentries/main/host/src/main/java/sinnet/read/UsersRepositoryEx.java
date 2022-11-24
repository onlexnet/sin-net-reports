package sinnet.read;

import java.util.List;
import java.util.UUID;

import sinnet.models.Email;

/** Provides some projections where User is the central entity. */
public interface UsersRepositoryEx {

  List<UserModel> search(UUID projectId, Email serviceMan);
}
