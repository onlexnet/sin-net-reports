package sinnet;

import java.util.UUID;

import org.springframework.stereotype.Service;

import io.vavr.collection.List;
import lombok.RequiredArgsConstructor;
import sinnet.domain.model.ValEmail;
import sinnet.models.ValName;
import sinnet.read.ServicemanRepo;
import sinnet.read.UserModel;
import sinnet.read.UsersRepositoryEx;

/**
 * TBD.
 */
@Service
@RequiredArgsConstructor
public class UsersProjectorImpl implements UsersRepositoryEx {

  private final ServicemanRepo repository;

  @Override
  public List<UserModel> search(UUID projectId) {
    return repository.findByProjectId(projectId)
        .map(it -> UserModel
            .builder()
            .email(ValEmail.of(it.getEmail()))
            .customName(ValName.of(it.getCustomName()))
            .build())
            .toList();
  }
}
