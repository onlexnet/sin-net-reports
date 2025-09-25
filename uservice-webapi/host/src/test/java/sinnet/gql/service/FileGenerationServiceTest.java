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
    String parameter = "testParameter";

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(parameter);

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
    String parameter = "testParameter";

    // When
    String base64Result = fileGenerationService.generateEmptyExcelAsBase64(parameter);

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
    String parameter = null;

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(parameter);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
  }

  @Test
  void generateEmptyExcelAsBase64_shouldWorkWithEmptyParameter() {
    // Given
    String parameter = "";

    // When
    String result = fileGenerationService.generateEmptyExcelAsBase64(parameter);

    // Then
    assertThat(result).isNotNull();
    assertThat(result).isNotEmpty();
  }

  @Test
  void generateLogicalFileName_shouldReturnDefaultName() {
    // Given
    String parameter = "anyValue";

    // When
    String fileName = fileGenerationService.generateLogicalFileName(parameter);

    // Then
    assertThat(fileName).isEqualTo("export.xlsx");
  }

  @Test
  void generateLogicalFileName_shouldReturnSameNameForDifferentParameters() {
    // Given
    String parameter1 = "parameter1";
    String parameter2 = "parameter2";

    // When
    String fileName1 = fileGenerationService.generateLogicalFileName(parameter1);
    String fileName2 = fileGenerationService.generateLogicalFileName(parameter2);

    // Then
    // Since parameter is currently ignored, both should return the same filename
    assertThat(fileName1).isEqualTo(fileName2);
    assertThat(fileName1).isEqualTo("export.xlsx");
  }

  @Test
  void generateLogicalFileName_shouldWorkWithNullParameter() {
    // Given
    String parameter = null;

    // When
    String fileName = fileGenerationService.generateLogicalFileName(parameter);

    // Then
    assertThat(fileName).isEqualTo("export.xlsx");
  }
}