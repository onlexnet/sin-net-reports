package sinnet.project.events;

import org.junit.jupiter.api.Test;

public class AvroDeSerTest {

  @Test
  void shouldDecode() {
    var data = "{\"eid\":\"281be26e-8c7f-4846-9a9e-70da8c358aba\",\"etag\":1}";
    var actual = AvroDeSer.fromJson(ProjectCreatedEvent.class, ProjectCreatedEvent.SCHEMA$, data);
  }
}
