package sinnet;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

@GraphQLApi
@ApplicationScoped
public class ActionsMutationNewAction {
  public @NonNull Entity newAction(@Source ActionsMutation self, @NonNull LocalDate whenProvided) {

        //     var maybeRequestor = identityProvider.getCurrent();
        // if (!maybeRequestor.isPresent()) {
        //   throw new GraphQLException("Access denied");
        // }

    return null;
  }
}
