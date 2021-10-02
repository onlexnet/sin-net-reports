package sinnet;

import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.microprofile.graphql.NonNull;

public class CustomersQuery {
  public CustomersQuery(UUID projectId) {
  }

  public @NonNull CustomerEntity[] getList() {
    return ArrayUtils.toArray();
  }
}
