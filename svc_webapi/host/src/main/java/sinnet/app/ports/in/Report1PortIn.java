package sinnet.app.ports.in;

import java.util.UUID;

/**
 * Port-in interface for report1 download operations.
 */
public interface Report1PortIn {

  /**
   * Downloads a PDF file for the given project and month.
   *
   * @param projectId the project UUID
   * @param year the year
   * @param month the month
   * @return the PDF bytes
   */
  byte[] downloadPdfFile(UUID projectId, int year, int month);

  /**
   * Downloads a ZIP file for the given project and month using the Azure Function proxy.
   *
   * @param projectId the project UUID
   * @param year the year
   * @param month the month
   * @return the ZIP bytes
   */
  byte[] downloadPdfFileUsingFunction(UUID projectId, int year, int month);
}