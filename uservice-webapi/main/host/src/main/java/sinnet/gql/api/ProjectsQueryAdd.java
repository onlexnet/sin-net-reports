package sinnet.gql.api;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Source;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.Array;
import lombok.extern.slf4j.Slf4j;
import sinnet.gql.Transform;
import sinnet.gql.models.CommonMapper;
import sinnet.gql.models.CustomerContactInput;
import sinnet.gql.models.CustomerInput;
import sinnet.gql.models.CustomerSecretExInput;
import sinnet.gql.models.CustomerSecretInput;
import sinnet.gql.models.Entity;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.Customers;
import sinnet.grpc.customers.UpdateCommand;

@GraphQLApi
@Slf4j
public class ProjectsQueryAdd implements CommonMapper {

  @GrpcClient("activities")
  Customers service;

  public @NonNull Uni<sinnet.gql.models.Entity> save(@Source ProjectsQueryAdd self, NonNull String name) {

  }
    
}
