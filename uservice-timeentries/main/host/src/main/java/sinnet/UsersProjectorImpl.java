package sinnet;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sinnet.models.Email;
import sinnet.read.ServicemanDbo;
import sinnet.read.ServicemanRepo;
import sinnet.read.UserModel;
import sinnet.read.UsersRepositoryEx;

@Service
@RequiredArgsConstructor
public class UsersProjectorImpl implements UsersRepositoryEx {

  private final ServicemanRepo repository;

  @Override
  public List<UserModel> search(UUID projectId, Email serviceMan) {
    var probe = new ServicemanDbo()
        .setEmail(serviceMan.getValue())
        .setProjectEntityId(projectId);
    var example = Example.of(probe);
    return repository
        .findAll(example)
        .stream()
        .map(it -> UserModel
            .builder()
            .email(Email.of(it.getEmail()))
            .build())
        .toList();
  }
}
