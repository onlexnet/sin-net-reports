package sinnet.gql.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for FileGenerationService.
 */
@ExtendWith(MockitoExtension.class)
class FileGenerationServiceTest {

  @InjectMocks
  private FileGenerationService fileGenerationService;

  @Test
  void generateEmptyExcelAsBase64_shouldReturnValidBase64() {
    // Given
    String projectId = "testProject";
    int year = 2024;
    int month = 12;

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(projectId, year, month);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
    
    // Verify it's valid base64
    try {
      Base64.getDecoder().decode(result);
    } catch (IllegalArgumentException e) {
      throw new AssertionError("Generated content is not valid base64", e);
    }
  }

  @Test
  void generateEmptyExcelAsBase64_shouldReturnValidExcelFile() throws IOException {
    // Given
    String projectId = "testProject";
    int year = 2024;
    int month = 12;

    // When
    String base64Result = fileGenerationService.generateEmptyExcelAsBase64(projectId, year, month);

    // Then
    byte[] excelBytes = Base64.getDecoder().decode(base64Result);
    
    // Verify it's a valid Excel file by opening it
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);
         XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
      
      // Should have at least one sheet
      assertThat(workbook.getNumberOfSheets()).isGreaterThan(0);
      assertThat(workbook.getSheetAt(0).getSheetName()).isEqualTo("Sheet1");
    }
  }

  @Test
  void generateEmptyExcelAsBase64_shouldWorkWithNullParameter() {
    // Given
    String projectId = null;
    int year = 2024;
    int month = 12;

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(projectId, year, month);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
  }

  @Test
  void generateEmptyExcelAsBase64_shouldWorkWithEmptyParameter() {
    // Given
    String projectId = "";
    int year = 2024;
    int month = 12;

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(projectId, year, month);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
  }

  @Test
  void generateLogicalFileName_shouldReturnFormattedName() {
    // Given
    String projectId = "project123";
    int year = 2024;
    int month = 5;

    // When
    String fileName = fileGenerationService.generateLogicalFileName(projectId, year, month);

    // Then
    assertThat(fileName).isEqualTo("export_project123_2024-05.xlsx");
  }

  @Test
  void generateLogicalFileName_shouldReturnDifferentNamesForDifferentParameters() {
    // Given
    String projectId1 = "project1";
    String projectId2 = "project2";
    int year = 2024;
    int month = 12;

    // When
    String fileName1 = fileGenerationService.generateLogicalFileName(projectId1, year, month);
    String fileName2 = fileGenerationService.generateLogicalFileName(projectId2, year, month);

    // Then
    assertThat(fileName1).isEqualTo("export_project1_2024-12.xlsx");
    assertThat(fileName2).isEqualTo("export_project2_2024-12.xlsx");
    assertThat(fileName1).isNotEqualTo(fileName2);
  }

  @Test
  void generateLogicalFileName_shouldWorkWithNullParameter() {
    // Given
    String projectId = null;
    int year = 2024;
    int month = 1;

    // When
    String fileName = fileGenerationService.generateLogicalFileName(projectId, year, month);

    // Then
    assertThat(fileName).isEqualTo("export_null_2024-01.xlsx");
  }
}