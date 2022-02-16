package sinnet;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class CustomersQueryGet {

  // DateTime, created and kept on server-side is UTC only. We can't send data to
  // client because GraphQL does not support Date / Time types.
  // So, we send a simple string without implicit contratc so that client may
  // convert them so its
  // proper models for date/time representation
  // reason: https://github.com/onlexnet/sin-net-reports/issues/59
  // private static DateTimeFormatter timestampFormatter = DateTimeFormatter.ISO_DATE_TIME;
  // .set(Option.of(value.getChangedWhen()).map(v -> timestampFormatter.format(v)).getOrElse("?"), b -> b::setChangedWhen)
  public CustomerEntity get(@Source CustomersQuery self, @Id UUID entityId) {
    return null;
  }

}
