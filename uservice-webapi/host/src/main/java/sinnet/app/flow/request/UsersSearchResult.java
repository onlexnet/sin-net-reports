package sinnet.app.flow.request;

import java.util.List;

public record UsersSearchResult(List<UsersSearchItem> items) {

  public record UsersSearchItem(String email, String entityId, String customName) {
  }
}