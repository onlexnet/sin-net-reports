package sinnet;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;

@GraphQLApi
@ApplicationScoped
public class ActionsMutationNewAction {
  public @NonNull SomeEntity newAction(@NonNull LocalDate whenProvided) {
    return null;
  }
}
