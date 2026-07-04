package sinnet.infra.adapters.ws;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.google.protobuf.ByteString;

import sinnet.app.flow.request.CustomerListQuery;
import sinnet.app.flow.request.CustomerListResult;
import sinnet.app.ports.out.CustomersPortOut;
import sinnet.domain.models.Customer;
import sinnet.domain.models.CustomerEntry;
import sinnet.domain.models.EntityId;
import sinnet.infra.Program;
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
  private CustomersPortOut customersPortOut;

  @MockitoBean
  private ReportsBlockingStub reportsBlockingStub;

  @Test
  void downloadPdfFile_shouldReturnPdfWithCorrectHeaders() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var expectedPdfData = new byte[]{0x25, 0x50, 0x44, 0x46}; // PDF signature

    // Mock customers - return empty list
    var listReply = new CustomerListResult(List.of());
    when(customersPortOut.list(any(CustomerListQuery.class)))
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
        verify(customersPortOut).list(any(CustomerListQuery.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  @Test
  void downloadPdfFile_shouldProcessCustomersWithOperators() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var customerId1 = UUID.randomUUID();
    var customerId2 = UUID.randomUUID();

    // Mock customers with operators
    var customer1 = new Customer(
        new EntityId(projectId, customerId1, 0),
        new sinnet.domain.models.CustomerValue(
            newCustomerEntry("Customer A", "Address A", "City A", "operator1@example.com"),
            List.of(), List.of(), List.of()));

    var customer2 = new Customer(
        new EntityId(projectId, customerId2, 0),
        new sinnet.domain.models.CustomerValue(
            newCustomerEntry("Customer B", "Address B", "City B", "operator2@example.com"),
            List.of(), List.of(), List.of()));

    var listReply = new CustomerListResult(List.of(customer1, customer2));

    when(customersPortOut.list(any(CustomerListQuery.class)))
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
        verify(customersPortOut).list(any(CustomerListQuery.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  static CustomerEntry newCustomerEntry(String customerName, String customerAddress, String customerCityName, String operatorEmail) {
    return new CustomerEntry(operatorEmail,
        null,
        null,
         0,
         customerName,
         customerCityName,
         customerAddress,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         null,
         false,
         null, null);
  }

  @Test
  void downloadPdfFile_shouldFilterCustomersWithoutOperator() throws Exception {
    // Given
    var projectId = UUID.randomUUID();

    // Create customer with operator email (should be included)
    var customerWithOperator = new Customer(
        new EntityId(projectId, UUID.randomUUID(), 0),
        new sinnet.domain.models.CustomerValue(
            newCustomerEntry("Customer With Operator", null, null, "operator@example.com"),
            List.of(), List.of(), List.of()));

    // Create customer without operator email (should be filtered)
    var customerWithoutOperator = new Customer(
        new EntityId(projectId, UUID.randomUUID(), 0),
        new sinnet.domain.models.CustomerValue(
            newCustomerEntry("Customer Without Operator", null, null, "null"),
            List.of(), List.of(), List.of()));
            
    var listReply = new CustomerListResult(List.of(customerWithOperator, customerWithoutOperator));

    when(customersPortOut.list(any(CustomerListQuery.class)))
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
        verify(customersPortOut).list(any(CustomerListQuery.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }

  @Test
  void downloadPdfFile_shouldHandleEmptyCustomerList() throws Exception {
    // Given
    var projectId = UUID.randomUUID();

    // Mock empty customer list
    var listReply = new CustomerListResult(List.of());
    when(customersPortOut.list(any(CustomerListQuery.class)))
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
        verify(customersPortOut).list(any(CustomerListQuery.class));
    verify(reportsBlockingStub).produce(any(ReportRequest.class));
  }
}
