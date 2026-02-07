package sinnet.gql.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import sinnet.gql.api.CommonMapper;
import sinnet.grpc.timeentries.TimeEntryModel;
import sinnet.ports.timeentries.ActionsGrpcFacade;
import sinnet.ports.timeentries.CustomersGrpcFacade;

/**
 * Unit tests for FileGenerationService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FileGenerationService")
class FileGenerationServiceTest {

  private static final String TEST_USER_EMAIL = "test@example.com";
  private static final int TEST_YEAR = 2024;
  private static final int TEST_MONTH = 12;
  private static final String SHEET_NAME = "Time Entries";

  @Mock
  private ActionsGrpcFacade actionsGrpcFacade;
  
  @Mock
  private CustomersGrpcFacade customersGrpcFacade;

  @Spy
  private CommonMapper commonMapper = Mappers.getMapper(CommonMapper.class);

  @InjectMocks
  private FileGenerationService fileGenerationService;

  private String testProjectId;

  @BeforeEach
  void setUp() {
    testProjectId = UUID.randomUUID().toString();
  }

  @Nested
  @DisplayName("generateExcelAsBase64")
  class GenerateExcelAsBase64Tests {

    @Test
    @DisplayName("should return valid base64 encoded content")
    void shouldReturnValidBase64() {
      // Given
      when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
          .thenReturn(List.of());
      when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
          .thenReturn(List.of());

      // When
      var result = fileGenerationService.generateExcelAsBase64(testProjectId, TEST_YEAR, TEST_MONTH, TEST_USER_EMAIL);

      // Then
      assertThat(result).isNotNull().isNotEmpty();
      assertThat(Base64.getDecoder().decode(result))
          .as("Result should be valid base64")
          .isNotEmpty();
    }

    @Test
    @DisplayName("should generate valid Excel file with correct structure")
    void shouldGenerateValidExcelFile() throws IOException {
      // Given
      when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
          .thenReturn(List.of());
      when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
          .thenReturn(List.of());

      // When
      var base64Result = fileGenerationService.generateExcelAsBase64(testProjectId, TEST_YEAR, TEST_MONTH, TEST_USER_EMAIL);

      // Then
      var excelBytes = Base64.getDecoder().decode(base64Result);
      
      try (var inputStream = new ByteArrayInputStream(excelBytes);
           var workbook = new XSSFWorkbook(inputStream)) {
        
        assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
        assertThat(workbook.getSheetAt(0).getSheetName()).isEqualTo(SHEET_NAME);
        assertThat(workbook.getSheetAt(0).getPhysicalNumberOfRows()).isEqualTo(1); // Header only
      }
    }

    @Test
    @DisplayName("should throw exception for null projectId")
    void shouldThrowExceptionForNullProjectId() {
      // When/Then
      assertThatThrownBy(() -> 
          fileGenerationService.generateExcelAsBase64(null, TEST_YEAR, TEST_MONTH, TEST_USER_EMAIL))
          .isNotNull();
    }

    @Test
    @DisplayName("should filter time entries by user email")
    void shouldFilterByUserEmail() throws IOException {
      // Given
      var johnEmail = "john@example.com";
      var janeEmail = "jane@example.com";
      
      var timeEntry1 = createTimeEntry(johnEmail, "Task 1", 120, 10, LocalDate.of(2024, 12, 15));
      var timeEntry2 = createTimeEntry(janeEmail, "Task 2", 90, 5, LocalDate.of(2024, 12, 16));
      
      when(actionsGrpcFacade.searchInternal(any(UUID.class), any(LocalDate.class), any(LocalDate.class)))
          .thenReturn(List.of(timeEntry1, timeEntry2));
      when(customersGrpcFacade.customerList(anyString(), anyString(), any()))
          .thenReturn(List.of());

      // When
      var base64Result = fileGenerationService.generateExcelAsBase64(testProjectId, TEST_YEAR, TEST_MONTH, johnEmail);

      // Then
      var excelBytes = Base64.getDecoder().decode(base64Result);
      
      try (var inputStream = new ByteArrayInputStream(excelBytes);
           var workbook = new XSSFWorkbook(inputStream)) {
        
        var sheet = workbook.getSheetAt(0);
        assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(2); // Header + 1 data row
        
        var dataRow = sheet.getRow(1);
        assertThat(dataRow.getCell(0).getStringCellValue()).isEqualTo("john");
        assertThat(dataRow.getCell(3).getStringCellValue()).isEqualTo("Task 1");
        assertThat(dataRow.getCell(4).getStringCellValue()).isEqualTo("2:00");
      }
    }
  }

  @Nested
  @DisplayName("generateLogicalFileName")
  class GenerateLogicalFileNameTests {

    @Test
    @DisplayName("should return formatted filename with year and month")
    void shouldReturnFormattedName() {
      // When
      var fileName = fileGenerationService.generateLogicalFileName(TEST_YEAR, 5);

      // Then
      assertThat(fileName).isEqualTo("export_2024-05.xlsx");
    }
  }

  private TimeEntryModel createTimeEntry(String email, String description, int duration, int distance, LocalDate date) {
    return TimeEntryModel.newBuilder()
        .setServicemanEmail(email)
        .setDescription(description)
        .setDuration(duration)
        .setDistance(distance)
        .setWhenProvided(commonMapper.toGrpc(date))
        .setCustomerId(UUID.randomUUID().toString())
        .build();
  }
}