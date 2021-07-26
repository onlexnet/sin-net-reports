package sinnet.gql.actions;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;

/** Fixme. */
@Component
public class ActionsQueryGet implements GraphQLResolver<ActionsQuery> {

  @Autowired
  private ActionsQueryInvoker invoker;

  public CompletionStage<ServiceModel> get(ActionsQuery gcontext, UUID actionId) {
    var projectId = gcontext.getProjectId();
    return invoker.find(projectId, actionId);
  }
}

