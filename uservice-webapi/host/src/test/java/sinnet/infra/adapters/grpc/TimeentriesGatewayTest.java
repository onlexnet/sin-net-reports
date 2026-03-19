package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import sinnet.domain.models.EntityId;
import sinnet.grpc.timeentries.GetQuery;
import sinnet.grpc.timeentries.GetReply;
import sinnet.grpc.timeentries.RemoveCommand;
import sinnet.grpc.timeentries.RemoveResult;
import sinnet.grpc.timeentries.ReserveCommand;
import sinnet.grpc.timeentries.ReserveResult;
import sinnet.grpc.timeentries.SearchQuery;
import sinnet.grpc.timeentries.SearchReply;
import sinnet.grpc.timeentries.TimeEntriesGrpc;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.grpc.timeentries.UpdateCommand;
import sinnet.grpc.timeentries.UpdateResult;

class TimeentriesGatewayTest {

  private Server server;
  private ManagedChannel channel;
  private TimeentriesGateway facade;
  private CapturingService capturingService;

  @BeforeEach
  void setUp() throws Exception {
    capturingService = new CapturingService();
    var serverName = InProcessServerBuilder.generateName();
    server = InProcessServerBuilder.forName(serverName)
        .directExecutor()
        .addService(capturingService)
        .build()
        .start();
    channel = InProcessChannelBuilder.forName(serverName)
        .directExecutor()
        .build();
    var stub = TimeEntriesGrpc.newBlockingStub(channel);
    facade = new TimeentriesGateway(stub);
  }

  @AfterEach
  void tearDown() throws Exception {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @Nested
  @DisplayName("update")
  class UpdateTests {

    @Test
    @DisplayName("should send all domain fields as gRPC UpdateCommand")
    void shouldSendAllDomainFieldsInUpdateCommand() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var entityVersion = 3L;
      var id = new EntityId(projectId, entityId, entityVersion);
      var whenProvided = LocalDate.of(2024, 5, 15);

      facade.update(id, "customer-123", "desc text", 42, 90,
          "tech@example.com", "Jan Kowalski", whenProvided);

      var captured = capturingService.lastUpdateCommand.get();
      assertThat(captured).isNotNull();
      var model = captured.getModel();
      assertThat(model.getEntityId().getProjectId()).isEqualTo(projectId.toString());
      assertThat(model.getEntityId().getEntityId()).isEqualTo(entityId.toString());
      assertThat(model.getEntityId().getEntityVersion()).isEqualTo(entityVersion);
      assertThat(model.getCustomerId()).isEqualTo("customer-123");
      assertThat(model.getDescription()).isEqualTo("desc text");
      assertThat(model.getDistance()).isEqualTo(42);
      assertThat(model.getDuration()).isEqualTo(90);
      assertThat(model.getServicemanEmail()).isEqualTo("tech@example.com");
      assertThat(model.getServicemanName()).isEqualTo("Jan Kowalski");
      assertThat(model.getWhenProvided().getYear()).isEqualTo(2024);
      assertThat(model.getWhenProvided().getMonth()).isEqualTo(5);
      assertThat(model.getWhenProvided().getDay()).isEqualTo(15);
    }
  }

  @Nested
  @DisplayName("remove")
  class RemoveTests {

    @Test
    @DisplayName("should send projectId, entityId and version in RemoveCommand")
    void shouldSendEntityIdInRemoveCommand() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();

      facade.remove(projectId, entityId, 7);

      var captured = capturingService.lastRemoveCommand.get();
      assertThat(captured).isNotNull();
      var grpcEntityId = captured.getEntityId();
      assertThat(grpcEntityId.getProjectId()).isEqualTo(projectId.toString());
      assertThat(grpcEntityId.getEntityId()).isEqualTo(entityId.toString());
      assertThat(grpcEntityId.getEntityVersion()).isEqualTo(7);
    }
  }

  @Nested
  @DisplayName("searchInternal")
  class SearchTests {

    @Test
    @DisplayName("should send projectId and date range in SearchQuery")
    void shouldSendProjectIdAndDateRangeInSearchQuery() {
      var projectId = UUID.randomUUID();
      var from = LocalDate.of(2024, 1, 1);
      var to = LocalDate.of(2024, 12, 31);

      facade.searchInternal(projectId, from, to);

      var captured = capturingService.lastSearchQuery.get();
      assertThat(captured).isNotNull();
      assertThat(captured.getProjectId()).isEqualTo(projectId.toString());
      assertThat(captured.getFrom().getYear()).isEqualTo(2024);
      assertThat(captured.getFrom().getMonth()).isEqualTo(1);
      assertThat(captured.getFrom().getDay()).isEqualTo(1);
      assertThat(captured.getTo().getYear()).isEqualTo(2024);
      assertThat(captured.getTo().getMonth()).isEqualTo(12);
      assertThat(captured.getTo().getDay()).isEqualTo(31);
    }
  }

  @Nested
  @DisplayName("getActionInternal")
  class GetActionTests {

    @Test
    @DisplayName("should send projectId and timeentryId in GetQuery")
    void shouldSendIdsInGetQuery() {
      var projectId = UUID.randomUUID();
      var timeentryId = UUID.randomUUID();

      facade.getActionInternal(projectId, timeentryId);

      var captured = capturingService.lastGetQuery.get();
      assertThat(captured).isNotNull();
      assertThat(captured.getProjectId()).isEqualTo(projectId.toString());
      assertThat(captured.getTimeentryId()).isEqualTo(timeentryId.toString());
    }
  }

  /** In-process gRPC server that captures incoming requests for assertions. */
  private static class CapturingService extends TimeEntriesGrpc.TimeEntriesImplBase {

    final AtomicReference<UpdateCommand> lastUpdateCommand = new AtomicReference<>();
    final AtomicReference<RemoveCommand> lastRemoveCommand = new AtomicReference<>();
    final AtomicReference<SearchQuery> lastSearchQuery = new AtomicReference<>();
    final AtomicReference<GetQuery> lastGetQuery = new AtomicReference<>();
    final AtomicReference<ReserveCommand> lastReserveCommand = new AtomicReference<>();

    @Override
    public void update(UpdateCommand request, StreamObserver<UpdateResult> responseObserver) {
      lastUpdateCommand.set(request);
      responseObserver.onNext(UpdateResult.newBuilder().setSuccess(true).build());
      responseObserver.onCompleted();
    }

    @Override
    public void remove(RemoveCommand request, StreamObserver<RemoveResult> responseObserver) {
      lastRemoveCommand.set(request);
      responseObserver.onNext(RemoveResult.newBuilder().setResult(true).build());
      responseObserver.onCompleted();
    }

    @Override
    public void search(SearchQuery request, StreamObserver<SearchReply> responseObserver) {
      lastSearchQuery.set(request);
      responseObserver.onNext(SearchReply.newBuilder().build());
      responseObserver.onCompleted();
    }

    @Override
    public void get(GetQuery request, StreamObserver<GetReply> responseObserver) {
      lastGetQuery.set(request);
      responseObserver.onNext(GetReply.newBuilder().setItem(TimeEntryModel.newBuilder()).build());
      responseObserver.onCompleted();
    }

    @Override
    public void reserve(ReserveCommand request, StreamObserver<ReserveResult> responseObserver) {
      lastReserveCommand.set(request);
      responseObserver.onNext(ReserveResult.newBuilder()
          .setEntityId(sinnet.grpc.common.EntityId.newBuilder()
              .setProjectId(request.getInvoker().getProjectId())
              .setEntityId(UUID.randomUUID().toString())
              .setEntityVersion(1))
          .build());
      responseObserver.onCompleted();
    }
  }
}
