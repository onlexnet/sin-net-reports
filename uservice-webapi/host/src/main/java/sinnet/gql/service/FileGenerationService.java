package sinnet.gql.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import sinnet.domain.AppException;

/**
 * Service for generating empty Excel files and converting them to base64.
 */
@Service
@Slf4j
public class FileGenerationService {

  /**
   * Generates an empty Excel file and returns it as base64 string.
   * The parameter value is currently ignored but can be used for future enhancements.
   *
   * @param parameter any string parameter (currently ignored)
   * @return base64 encoded empty Excel file
   */
  public String generateEmptyExcelAsBase64(String parameter) {
    log.info("Generating empty Excel file for parameter: {}", parameter);
    
    try (XSSFWorkbook workbook = new XSSFWorkbook();
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      
      // Create an empty sheet
      workbook.createSheet("Sheet1");
      
      // Write workbook to byte array
      workbook.write(outputStream);
      
      // Convert to base64
      var bytes = outputStream.toByteArray();
      var base64Content = Base64.getEncoder().encodeToString(bytes);
      
      log.info("Successfully generated empty Excel file with {} bytes", bytes.length);
      return base64Content;
      
    } catch (IOException e) {
      log.error("Failed to generate Excel file", e);
      throw new AppException("Failed to generate Excel file", e);
    }
  }

  /**
   * Generates a logical filename based on the parameter.
   * The parameter value is currently ignored and returns a default filename.
   *
   * @param parameter any string parameter (currently ignored)
   * @return logical filename for the Excel file
   */
  public String generateLogicalFileName(String parameter) {
    // For now, return a default filename. The parameter is ignored as requested.
    // In the future, this could be enhanced to use the parameter value.
    return "export.xlsx";
  }
}