package sinnet;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.microprofile.graphql.NonNull;

import lombok.Data;

@Data
public class ServicesSearchResult {
  private @NonNull ServiceModel[] items = ArrayUtils.toArray();
  private int totalDistance;
}
