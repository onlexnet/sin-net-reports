package sinnet.gql.models;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/** GQL model. */
@Data
@Accessors(chain = true)
public class ServicesSearchResultGql {
  private List<ServiceModelGql> items;
}
