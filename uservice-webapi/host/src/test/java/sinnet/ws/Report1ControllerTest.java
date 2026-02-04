package sinnet.ws;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
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

import sinnet.app.Program;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.grpc.users.SearchRequest;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.ports.timeentries.CustomersGrpcFacade;
import sinnet.ports.timeentries.Reports1GrpcAdapter;
import sinnet.ports.timeentries.UsersGrpcService;
import sinnet.report1.grpc.ReportRequests;

/**
 * Unit tests for Report1Controller using MockMvc to test HTTP layer.
 */
@SpringBootTest(classes = Program.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Report1ControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ActionsGrpcFacade actionsGrpcFacade;

  @MockitoBean
  private CustomersGrpcFacade customersGrpcFacade;

  @MockitoBean
  private Reports1GrpcAdapter reports1GrpcAdapter;

  @MockitoBean
  private UsersGrpcService usersGrpcService;

  @Test
  void downloadPdfFile_shouldReturnZipFileWithCorrectHeaders() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var year = 2024;
    var month = 3;
    var expectedZipData = new byte[]{0x50, 0x4B, 0x03, 0x04}; // ZIP file signature

    // Mock actions/time entries - return empty list
    when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());

    // Mock customers - return empty list
    when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
        .thenReturn(List.of());

    // Mock users - return empty reply
    var usersSearchReply = sinnet.grpc.users.SearchReply.newBuilder().build();
    when(usersGrpcService.search(any(SearchRequest.class)))
        .thenReturn(usersSearchReply);

    // Mock report generation - return Response with zip data
    var reportResponse = sinnet.reports.grpc.Response.newBuilder()
        .setData(ByteString.copyFrom(expectedZipData))
        .build();
    when(reports1GrpcAdapter.producePack(any(ReportRequests.class)))
        .thenReturn(reportResponse);

    // When & Then
    mockMvc.perform(get("/api/raporty/klienci/{projectId}/{year}/{month}", projectId, year, month))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/zip"))
        .andExpect(header().string("Content-Disposition", "inline; filename=report 2024-3.zip"))
        .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
        .andExpect(header().string("Expires", "0"))
        .andExpect(header().string("Content-Length", String.valueOf(expectedZipData.length)))
        .andExpect(content().bytes(expectedZipData));

    // Verify interactions
    verify(actionsGrpcFacade).searchInternal(
        eq(projectId),
        eq(LocalDate.of(2024, 3, 1)),
        eq(LocalDate.of(2024, 3, 31))
    );
    verify(customersGrpcFacade).customerList(eq(projectId.toString()), anyString(), any());
    verify(usersGrpcService).search(any(SearchRequest.class));
    verify(reports1GrpcAdapter).producePack(any(ReportRequests.class));
  }

  @Test
  void downloadPdfFile_shouldHandleFebruaryCorrectly() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var year = 2024; // Leap year
    var month = 2;
    var expectedZipData = new byte[]{0x01, 0x02, 0x03};

    // Setup mocks
    when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());
    when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
        .thenReturn(List.of());
    when(usersGrpcService.search(any(SearchRequest.class)))
        .thenReturn(sinnet.grpc.users.SearchReply.newBuilder().build());
    when(reports1GrpcAdapter.producePack(any(ReportRequests.class)))
        .thenReturn(sinnet.reports.grpc.Response.newBuilder()
            .setData(ByteString.copyFrom(expectedZipData))
            .build());

    // When & Then
    mockMvc.perform(get("/api/raporty/klienci/{projectId}/{year}/{month}", projectId, year, month))
        .andExpect(status().isOk());

    // Verify date range for February in leap year
    verify(actionsGrpcFacade).searchInternal(
        eq(projectId),
        eq(LocalDate.of(2024, 2, 1)),
        eq(LocalDate.of(2024, 2, 29)) // Leap year
    );
  }

  @Test
  void downloadPdfFile_shouldProcessTimeEntriesWithCustomers() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var customerId = UUID.randomUUID().toString();
    var year = 2024;
    var month = 5;

    // Mock time entries
    var actionItem = TimeEntryModel.newBuilder()
        .setCustomerId(customerId)
        .setDescription("Test action")
        .setWhenProvided(sinnet.grpc.timeentries.LocalDate.newBuilder()
            .setYear(2024)
            .setMonth(5)
            .setDay(15)
            .build())
        .setServicemanName("test@example.com")
        .setDistance(100)
        .setDuration(120)
        .build();
    
    when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of(actionItem));

    // Mock customers using CustomerModel
    var customerModel = new Report1Controller.CustomerModel(
        customerId,
        "Test Customer",
        "Test City",
        "Test Address"
    );
    
    when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
        .thenReturn(List.of(customerModel));

    // Mock users - return empty reply for simplicity
    var usersReply = sinnet.grpc.users.SearchReply.newBuilder()
        .build();
    when(usersGrpcService.search(any(SearchRequest.class)))
        .thenReturn(usersReply);

    // Mock report generation
    var reportData = new byte[]{0x01, 0x02};
    when(reports1GrpcAdapter.producePack(any(ReportRequests.class)))
        .thenReturn(sinnet.reports.grpc.Response.newBuilder()
            .setData(ByteString.copyFrom(reportData))
            .build());

    // When & Then
    mockMvc.perform(get("/api/raporty/klienci/{projectId}/{year}/{month}", projectId, year, month))
        .andExpect(status().isOk())
        .andExpect(content().bytes(reportData));

    // Verify all services were called
    verify(actionsGrpcFacade).searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class));
    verify(customersGrpcFacade).customerList(anyString(), anyString(), any());
    verify(usersGrpcService).search(any(SearchRequest.class));
    verify(reports1GrpcAdapter).producePack(any(ReportRequests.class));
  }

  @Test
  void downloadPdfFile_shouldHandleDecember() throws Exception {
    // Given
    var projectId = UUID.randomUUID();
    var year = 2024;
    var month = 12;

    // Setup mocks
    when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(List.of());
    when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
        .thenReturn(List.of());
    when(usersGrpcService.search(any(SearchRequest.class)))
        .thenReturn(sinnet.grpc.users.SearchReply.newBuilder().build());
    when(reports1GrpcAdapter.producePack(any(ReportRequests.class)))
        .thenReturn(sinnet.reports.grpc.Response.newBuilder()
            .setData(ByteString.copyFrom(new byte[]{0x01}))
            .build());

    // When & Then
    mockMvc.perform(get("/api/raporty/klienci/{projectId}/{year}/{month}", projectId, year, month))
        .andExpect(status().isOk());

    // Verify date range for December
    verify(actionsGrpcFacade).searchInternal(
        eq(projectId),
        eq(LocalDate.of(2024, 12, 1)),
        eq(LocalDate.of(2024, 12, 31))
    );
  }
}
