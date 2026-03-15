package sinnet.app.flow.userssearch;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import sinnet.app.flow.request.UsersSearchQuery;
import sinnet.app.flow.request.UsersSearchResult;
import sinnet.app.ports.in.UsersSearchPortIn;
import sinnet.app.ports.out.UsersServicePortOut;
import sinnet.domain.models.Email;

@Component
@RequiredArgsConstructor
class UsersSearchFlow implements UsersSearchPortIn {

  private final UsersServicePortOut service;

  @Override
  public UsersSearchResult search(UsersSearchQuery query) {
    return service.search(query.projectId(), Email.of(query.requestorEmail()));
  }

}
