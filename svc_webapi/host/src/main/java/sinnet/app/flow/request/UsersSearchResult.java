package sinnet.app.flow.request;

import java.util.List;

public record UsersSearchResult(List<Item> items) {

  public record Item(String email, String entityId, String customName) {
  }
}