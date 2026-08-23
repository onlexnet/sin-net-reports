package sinnet.app.ports.in;

import java.util.UUID;

import sinnet.app.ports.out.Report1FunctionOutPort.ReportLink;

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
   * Requests ZIP generation for the given project and month via the Azure Function proxy.
   *
   * @param projectId the project UUID
   * @param year the year
   * @param month the month
   * @return a time-limited download link to the generated ZIP archive
   */
  ReportLink downloadPdfFileUsingFunction(UUID projectId, int year, int month);
}