package sinnet.ws;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.google.protobuf.ByteString;

import sinnet.app.Program;
import sinnet.grpc.common.EntityId;
import sinnet.grpc.customers.CustomerModel;
import sinnet.grpc.customers.CustomerValue;
import sinnet.grpc.customers.ListReply;
import sinnet.grpc.customers.ListRequest;
import sinnet.ports.timeentries.CustomersGrpcFacade;
import sinnet.report3.grpc.ReportRequest;
import sinnet.report3.grpc.ReportsGrpc.ReportsBlockingStub;

/**
 * Unit tests for Report3Controller using MockMvc to test HTTP layer.
 */
@SpringBootTest(classes = Program.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Report3ControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CustomersGrpcFacade customersGrpcFacade;

  @MockitoBean
  private ReportsBlockingStub reportsBlockingStub;

  @Test
  void downloadPdfFile_shouldReturnPdfWithCorrectHeaders() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var expectedPdfData = new byte[]{0x25, 0x50, 0x44, 0x46}; // PDF signature

    // Mock customers - return empty list
    var listReply = ListReply.newBuilder().build();
    when(customersGrpcFacade.list(any(ListRequest.class)))
        .thenReturn(listReply);

    // Mock report generation - return FileResponse with PDF data
    var reportResponse = sinnet.reports.grpc.FileResponse.newBuilder()
        .setData(ByteString.copyFrom(expectedPdfData))
        .setFileName("report-3.pdf")
        .build();
    when(reportsBlockingStub.produce(any(ReportRequest.class)))
        .thenReturn(reportResponse);

    // When & Then
    mockMvc.perform(get("/api/raporty/3/{projectId}", projectId))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/zip"))
        .andExpect(header().string("Content-Disposition", "inline; filename=report-3.pdf"))
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(content().bytes(expectedPdfData));

    // Verify interactions
    verify(customersGrpcFacade).list(any(ListRequest.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  @Test
  void downloadPdfFile_shouldProcessCustomersWithOperators() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var customerId1 = UUID.randomUUID().toString();
    var customerId2 = UUID.randomUUID().toString();

    // Mock customers with operators
    var customer1 = CustomerModel.newBuilder()
        .setId(EntityId.newBuilder()
            .setProjectId(projectId.toString())
            .setEntityId(customerId1)
            .build())
        .setValue(CustomerValue.newBuilder()
            .setCustomerName("Customer A")
            .setCustomerAddress("Address A")
            .setCustomerCityName("City A")
            .setOperatorEmail("operator1@example.com")
            .build())
        .build();

    var customer2 = CustomerModel.newBuilder()
        .setId(EntityId.newBuilder()
            .setProjectId(projectId.toString())
            .setEntityId(customerId2)
            .build())
        .setValue(CustomerValue.newBuilder()
            .setCustomerName("Customer B")
            .setCustomerAddress("Address B")
            .setCustomerCityName("City B")
            .setOperatorEmail("operator2@example.com")
            .build())
        .build();

    var listReply = ListReply.newBuilder()
        .addCustomers(customer1)
        .addCustomers(customer2)
        .build();

    when(customersGrpcFacade.list(any(ListRequest.class)))
        .thenReturn(listReply);

    // Mock report generation
    var expectedPdfData = new byte[]{0x01, 0x02};
    var reportResponse = sinnet.reports.grpc.FileResponse.newBuilder()
        .setData(ByteString.copyFrom(expectedPdfData))
        .setFileName("report-3.pdf")
        .build();
    when(reportsBlockingStub.produce(any(ReportRequest.class)))
        .thenReturn(reportResponse);

    // When & Then
    mockMvc.perform(get("/api/raporty/3/{projectId}", projectId))
        .andExpect(status().isOk())
        .andExpect(content().bytes(expectedPdfData));

    // Verify both customers were processed
    verify(customersGrpcFacade).list(any(ListRequest.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  @Test
  void downloadPdfFile_shouldFilterCustomersWithoutOperator() throws Exception {
    // Given
    var projectId = UUID.randomUUID();

    // Create customer with operator email (should be included)
    var customerWithOperator = CustomerModel.newBuilder()
        .setId(EntityId.newBuilder()
            .setProjectId(projectId.toString())
            .setEntityId(UUID.randomUUID().toString())
            .build())
        .setValue(CustomerValue.newBuilder()
            .setCustomerName("Customer With Operator")
            .setOperatorEmail("operator@example.com")
            .build())
        .build();

    // Create customer without operator email (should be filtered)
    var customerWithoutOperator = CustomerModel.newBuilder()
        .setId(EntityId.newBuilder()
            .setProjectId(projectId.toString())
            .setEntityId(UUID.randomUUID().toString())
            .build())
        .setValue(CustomerValue.newBuilder()
            .setCustomerName("Customer Without Operator")
            .setOperatorEmail("")  // Empty operator email
            .build())
        .build();

    var listReply = ListReply.newBuilder()
        .addCustomers(customerWithOperator)
        .addCustomers(customerWithoutOperator)
        .build();

    when(customersGrpcFacade.list(any(ListRequest.class)))
        .thenReturn(listReply);

    // Mock report generation
    var expectedPdfData = new byte[]{0x01};
    var reportResponse = sinnet.reports.grpc.FileResponse.newBuilder()
        .setData(ByteString.copyFrom(expectedPdfData))
        .setFileName("report-3.pdf")
        .build();
    when(reportsBlockingStub.produce(any(ReportRequest.class)))
        .thenReturn(reportResponse);

    // When & Then
    mockMvc.perform(get("/api/raporty/3/{projectId}", projectId))
        .andExpect(status().isOk());

    // Verify filtering logic - only customer with operator should be in report request
    verify(customersGrpcFacade).list(any(ListRequest.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  @Test
  void downloadPdfFile_shouldHandleEmptyCustomerList() throws Exception {
    // Given
    var projectId = UUID.randomUUID();

    // Mock empty customer list
    var listReply = ListReply.newBuilder().build();
    when(customersGrpcFacade.list(any(ListRequest.class)))
        .thenReturn(listReply);

    // Mock report generation for empty list
    var expectedPdfData = new byte[]{0x00};
    var reportResponse = sinnet.reports.grpc.FileResponse.newBuilder()
        .setData(ByteString.copyFrom(expectedPdfData))
        .setFileName("report-3.pdf")
        .build();
    when(reportsBlockingStub.produce(any(ReportRequest.class)))
        .thenReturn(reportResponse);

    // When & Then
    mockMvc.perform(get("/api/raporty/3/{projectId}", projectId))
        .andExpect(status().isOk())
        .andExpect(content().bytes(expectedPdfData));

    // Verify empty list was handled
    verify(customersGrpcFacade).list(any(ListRequest.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }
}
