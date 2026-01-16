package sinnet.gql.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sinnet.domain.AppException;
import sinnet.gql.api.CommonMapper;
import sinnet.grpc.customers.CustomerModel;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.ports.timeentries.CustomersGrpcFacade;

/**
 * Service for generating Excel files with time entries and converting them to base64.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileGenerationService {

  private final ActionsGrpcFacade actionsGrpcFacade;
  private final CustomersGrpcFacade customersGrpcFacade;

  /**
   * Generates an Excel file with time entries for the logged-in user and returns it as base64 string.
   *
   * @param projectId the project ID
   * @param year the year for the report
   * @param month the month for the report
   * @param userEmail the email of the logged-in user
   * @return base64 encoded Excel file with time entries
   */
  public String generateExcelAsBase64(String projectId, int year, int month, String userEmail) {
    log.info("Generating Excel file for projectId: {}, year: {}, month: {}, user: {}", projectId, year, month, userEmail);
    
    try (var workbook = new XSSFWorkbook();
         var outputStream = new ByteArrayOutputStream()) {
      
      var projectIdUuid = UUID.fromString(projectId);
      
      // Calculate date range
      var dateFrom = LocalDate.of(year, month, 1);
      var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
      
      // Fetch time entries for the date range
      var allEntries = actionsGrpcFacade.searchInternal(projectIdUuid, dateFrom, dateTo);
      
      // Filter by logged-in user's email
      var userEntries = allEntries.stream()
          .filter(entry -> userEmail.equalsIgnoreCase(entry.getServicemanEmail()))
          .collect(Collectors.toList());
      
      log.info("Found {} time entries for user {}", userEntries.size(), userEmail);
      
      // Create sheet with data
      var sheet = workbook.createSheet("Time Entries");
      
      // Create header row
      var headerRow = sheet.createRow(0);
      headerRow.createCell(0).setCellValue("Employee");
      headerRow.createCell(1).setCellValue("Date");
      headerRow.createCell(2).setCellValue("Customer");
      headerRow.createCell(3).setCellValue("Description");
      headerRow.createCell(4).setCellValue("Duration");
      headerRow.createCell(5).setCellValue("Distance");
      
      // Build customer lookup map and extract username
      var customerLookup = buildCustomerLookup(projectId, userEmail);
      var username = extractUsername(userEmail);
      
      // Populate data rows
      for (int i = 0; i < userEntries.size(); i++) {
        var entry = userEntries.get(i);
        var row = sheet.createRow(i + 1);
        
        var date = CommonMapper.fromGrpc(entry.getWhenProvided());
        var customerName = customerLookup.getOrDefault(entry.getCustomerId(), "(unknown)");
        var durationFormatted = formatDuration(entry.getDuration());
        
        row.createCell(0).setCellValue(username);
        row.createCell(1).setCellValue(DateTimeFormatter.ISO_LOCAL_DATE.format(date));
        row.createCell(2).setCellValue(customerName);
        row.createCell(3).setCellValue(entry.getDescription());
        row.createCell(4).setCellValue(durationFormatted);
        row.createCell(5).setCellValue(entry.getDistance());
      }
      
      // Write workbook to byte array
      workbook.write(outputStream);
      
      // Convert to base64
      var bytes = outputStream.toByteArray();
      var base64Content = Base64.getEncoder().encodeToString(bytes);
      
      log.info("Successfully generated Excel file with {} bytes and {} entries", bytes.length, userEntries.size());
      return base64Content;
      
    } catch (IOException e) {
      log.error("Failed to generate Excel file", e);
      throw new AppException("Failed to generate Excel file", e);
    }
  }
  
  /**
   * Builds a lookup map of customer IDs to customer names.
   */
  private Map<String, String> buildCustomerLookup(String projectId, String userEmail) {
    List<CustomerModel> customers = customersGrpcFacade.customerList(projectId, userEmail, customer -> customer);
    
    return customers.stream()
        .collect(Collectors.toMap(
            customer -> customer.getId().getEntityId(),
            customer -> customer.getValue().getCustomerName(),
            (existing, replacement) -> existing,
            HashMap::new
        ));
  }
  
  /**
   * Formats duration in minutes to "H:MM" format.
   */
  private String formatDuration(int durationInMinutes) {
    int hours = durationInMinutes / 60;
    int minutes = durationInMinutes % 60;
    return String.format("%d:%02d", hours, minutes);
  }
  
  /**
   * Extracts username from email address (part before @).
   */
  private String extractUsername(String email) {
    if (email != null && email.contains("@")) {
      return email.split("@")[0];
    }
    return email;
  }

  /**
   * Generates a logical filename based on username, year and month.
   *
   * @param year the year for the report
   * @param month the month for the report
   * @return logical filename for the Excel file
   */
  public String generateLogicalFileName(int year, int month) {
    return String.format("export_%d-%02d.xlsx", year, month);
  }
}