package sinnet.action;

import io.vertx.sqlclient.Row;
import sinnet.models.ActionDuration;
import sinnet.models.ActionValue;
import sinnet.models.Distance;
import sinnet.models.Email;
import sinnet.models.Entity;

final class Mapper {

  /** Utility classes should not have a public or default constructor. */
  private Mapper() {
  }

  static Entity<ActionValue> map(Row row) {
    return ActionValue.builder()
      .who(Email.of(row.getString("serviceman_email")))
      .howFar(Distance.of(row.getInteger("distance")))
      .howLong(ActionDuration.of(row.getInteger("duration")))
      .whom(row.getUUID("customer_id"))
      .what(row.getString("description"))
      .when(row.getLocalDate("date"))
      .build()
      .withId(row.getUUID("project_id"), row.getUUID("entity_id"), row.getInteger("entity_version"));
  }

}
