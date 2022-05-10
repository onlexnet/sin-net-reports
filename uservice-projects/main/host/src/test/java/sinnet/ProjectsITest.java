package sinnet;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import sinnet.grpc.projects.AvailableProject;
import sinnet.grpc.projects.ListRequest;
import sinnet.grpc.projects.ProjectsGrpc;
import sinnet.grpc.projects.ReserveRequest;
import sinnet.grpc.projects.UpdateCommand;

@QuarkusTest
@QuarkusTestResource(InitDbTestResource.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProjectsITest {

  @Inject
  @GrpcClient
  ProjectsGrpc.ProjectsBlockingStub self;

  @Test
  public void happy_path() {
    var reserveCmd = ReserveRequest.newBuilder().build();
    var reserveResult = self.reserve(reserveCmd);

    var updateCmd = UpdateCommand.newBuilder()
        .setEntityId(reserveResult.getEntityId())
        .setName("my name")
        .setEmailOfOwner("owner@home")
        .build();
    self.update(updateCmd);

    var listQuery = ListRequest.newBuilder()
        .setEmailOfRequestor("owner@home")
        .build();
    var listResponse = self.list(listQuery);
    var items = listResponse.getProjectsList();

    var expected = AvailableProject.newBuilder()
        .setId(items.get(0).getId())
        .setName("my name")
        .build();
    Assertions.assertThat(items).containsExactly(expected);
  }

}