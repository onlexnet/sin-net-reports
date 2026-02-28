package sinnet.app.userssearch;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.UsersSearchPortIn;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.Email;

@Component
@RequiredArgsConstructor
class UsersSearchFlow implements UsersSearchPortIn {

  private final UsersServicePortOut service;

  @Override
  public Iterable<User> search(UUID projectId, String primaryEmail) {

    var result = service.search(projectId, Email.of(primaryEmail));

    return result.getItemsList().stream()
        .map(it -> new User(it.getEmail(), it.getEntityId()))
        .toList();

  }

}
