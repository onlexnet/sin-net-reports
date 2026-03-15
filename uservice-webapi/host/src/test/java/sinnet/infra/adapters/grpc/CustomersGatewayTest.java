package sinnet.infra.adapters.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sinnet.app.flow.request.CustomerGetQuery;
import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerRemoveResult;
import sinnet.app.flow.request.CustomerReserveCommand;
import sinnet.app.flow.request.CustomerReserveResult;
import sinnet.app.flow.request.CustomerUpdateCommand;
import sinnet.app.flow.request.CustomerUpdateResult;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.CustomerValue;
import sinnet.domain.models.EntityId;
import sinnet.domain.models.UserToken;
import sinnet.grpc.customers.CustomersGrpc.CustomersBlockingStub;
import sinnet.infra.adapters.grpc.CustomersGateway;
import sinnet.grpc.customers.GetReply;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.RemoveReply;
import sinnet.grpc.customers.ReserveReply;
import sinnet.grpc.customers.UpdateResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomersGateway")
class CustomersGatewayTest {

  @Mock
  private CustomersBlockingStub stub;

  @InjectMocks
  private CustomersGateway gateway;

  private static sinnet.grpc.customers.CustomerModel minimalModel(UUID projectId, UUID entityId, long version) {
    return sinnet.grpc.customers.CustomerModel.newBuilder()
        .setId(sinnet.grpc.common.EntityId.newBuilder()
            .setProjectId(projectId.toString())
            .setEntityId(entityId.toString())
            .setEntityVersion(version)
            .build())
        .build();
  }

  private static CustomerValue emptyCustomerValue() {
    var entry = new CustomerEntry(null, null, null, 0, null, null, null,
        false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
        null, false, null, null);
    return new CustomerValue(entry, List.of(), List.of(), List.of());
  }

  @Nested
  @DisplayName("list")
  class ListTests {

    @Test
    @DisplayName("should return empty list when backend returns no customers")
    void shouldReturnEmptyCustomers() {
      var projectId = UUID.randomUUID();
      var query = new CustomerListQuery(new UserToken(projectId, "user@example.com"));
      when(stub.list(any())).thenReturn(ListReply.newBuilder().build());

      var result = gateway.list(query);

      assertThat(result.customers()).isEmpty();
    }

    @Test
    @DisplayName("should map customer models returned by backend")
    void shouldMapCustomers() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var query = new CustomerListQuery(new UserToken(projectId, "user@example.com"));
      var model = minimalModel(projectId, entityId, 3L);
      when(stub.list(any())).thenReturn(ListReply.newBuilder().addCustomers(model).build());

      var result = gateway.list(query);

      assertThat(result.customers()).hasSize(1);
      assertThat(result.customers().get(0).id()).isEqualTo(new EntityId(projectId, entityId, 3L));
    }
  }

  @Nested
  @DisplayName("get")
  class GetTests {

    @Test
    @DisplayName("should map GetReply model to CustomerGetResult")
    void shouldMapGetReplyToResult() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var query = new CustomerGetQuery(new UserToken(projectId, "user@example.com"), entityId.toString());
      var model = minimalModel(projectId, entityId, 2L);
      when(stub.get(any())).thenReturn(GetReply.newBuilder().setModel(model).build());

      var result = gateway.get(query);

      assertThat(result.id()).isEqualTo(new EntityId(projectId, entityId, 2L));
    }
  }

  @Nested
  @DisplayName("reserve")
  class ReserveTests {

    @Test
    @DisplayName("should return reserved entity id from backend reply")
    void shouldReturnReservedEntityId() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var grpcEntityId = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId(projectId.toString())
          .setEntityId(entityId.toString())
          .setEntityVersion(0L)
          .build();
      when(stub.reserve(any())).thenReturn(ReserveReply.newBuilder().setEntityId(grpcEntityId).build());

      var result = gateway.reserve(new CustomerReserveCommand(projectId));

      assertThat(result).isEqualTo(new CustomerReserveResult(new EntityId(projectId, entityId, 0L)));
    }
  }

  @Nested
  @DisplayName("remove")
  class RemoveTests {

    @Test
    @DisplayName("should return success=true when backend confirms removal")
    void shouldReturnSuccessTrue() {
      var projectId = UUID.randomUUID();
      var domainEntityId = new EntityId(projectId, UUID.randomUUID(), 1L);
      when(stub.remove(any())).thenReturn(RemoveReply.newBuilder().setSuccess(true).build());

      var result = gateway.remove(domainEntityId, new UserToken(projectId, "user@example.com"));

      assertThat(result).isEqualTo(new CustomerRemoveResult(true));
    }

    @Test
    @DisplayName("should return success=false when backend rejects removal")
    void shouldReturnSuccessFalse() {
      var projectId = UUID.randomUUID();
      var domainEntityId = new EntityId(projectId, UUID.randomUUID(), 1L);
      when(stub.remove(any())).thenReturn(RemoveReply.newBuilder().setSuccess(false).build());

      var result = gateway.remove(domainEntityId, new UserToken(projectId, "user@example.com"));

      assertThat(result).isEqualTo(new CustomerRemoveResult(false));
    }
  }

  @Nested
  @DisplayName("update")
  class UpdateTests {

    @Test
    @DisplayName("should map UpdateResult entity id to CustomerUpdateResult")
    void shouldMapUpdateResultEntityId() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var grpcEntityId = sinnet.grpc.common.EntityId.newBuilder()
          .setProjectId(projectId.toString())
          .setEntityId(entityId.toString())
          .setEntityVersion(5L)
          .build();
      when(stub.update(any())).thenReturn(UpdateResult.newBuilder().setEntityId(grpcEntityId).build());

      var cmd = new CustomerUpdateCommand(
          new UserToken(projectId, "user@example.com"),
          new EntityId(projectId, entityId, 5L),
          emptyCustomerValue(),
          LocalDateTime.now(),
          "user@example.com");

      var result = gateway.update(cmd);

      assertThat(result).isEqualTo(new CustomerUpdateResult(new EntityId(projectId, entityId, 5L)));
    }
  }

  @Nested
  @DisplayName("customerGet (string params)")
  class CustomerGetStringTests {

    @Test
    @DisplayName("should map GetReply model to CustomerGetResult using string params")
    void shouldMapGetReplyToResult() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var model = minimalModel(projectId, entityId, 4L);
      when(stub.get(any())).thenReturn(GetReply.newBuilder().setModel(model).build());

      var result = gateway.customerGet(projectId.toString(), "user@example.com", entityId.toString());

      assertThat(result.id()).isEqualTo(new EntityId(projectId, entityId, 4L));
    }
  }

  @Nested
  @DisplayName("customerList (string params + mapper)")
  class CustomerListStringTests {

    @Test
    @DisplayName("should apply mapper function to each customer in the list")
    void shouldApplyMapperFunction() {
      var projectId = UUID.randomUUID();
      var entityId = UUID.randomUUID();
      var model = minimalModel(projectId, entityId, 1L);
      when(stub.list(any())).thenReturn(ListReply.newBuilder().addCustomers(model).build());

      var result = gateway.customerList(projectId.toString(), "user@example.com", customer -> customer.id());

      assertThat(result).containsExactly(new EntityId(projectId, entityId, 1L));
    }

    @Test
    @DisplayName("should return empty list when backend returns no customers")
    void shouldReturnEmptyList() {
      var projectId = UUID.randomUUID();
      when(stub.list(any())).thenReturn(ListReply.newBuilder().build());

      var result = gateway.customerList(projectId.toString(), "user@example.com", customer -> customer.id());

      assertThat(result).isEmpty();
    }
  }
}
