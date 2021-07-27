package sinnet.gql.actions;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLResolver;
import sinnet.gql.customers.CustomerEntity;
import sinnet.read.CustomerProjection;

/** Fixme. */
@Component
class ServiceModelResolvers implements GraphQLResolver<ServiceModel> {

  @Autowired
  private CustomerProjection invoker;

  public CompletionStage<Optional<CustomerEntity>> customer(ServiceModel gcontext) {
    var projectId = gcontext.getProjectId();
    var customerId = gcontext.getLocalCustomerId();
    if (customerId == null) {
      return CompletableFuture.completedStage(Optional.empty());
    }
    return invoker.get(projectId, customerId)
      .map(sinnet.gql.customers.Mapper::gql)
      .toCompletionStage();
  }
}
