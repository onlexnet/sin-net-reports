package sinnet.infra.adapters.ws;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.Report1PortIn;
import sinnet.app.ports.out.Report1FileNaming;
import sinnet.app.ports.out.Report1FunctionOutPort.ReportLink;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report1Adapter {

  private final Report1PortIn report1PortIn;

  @GetMapping("/klienci/{projectId}/{year}/{month}")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId, @PathVariable int year, @PathVariable int month) {
    var result = report1PortIn.downloadPdfFile(projectId, year, month);
    return Response.asResponseEntity(result, Report1FileNaming.zipFileName(year, month));
  }

  /**
   * Requests ZIP generation via fun_report1 and returns a time-limited download link as JSON,
   * instead of proxying the archive's bytes through webapi.
   */
  @GetMapping("/klienci-fun/{projectId}/{year}/{month}")
  public ReportLink getReportZipLinkUsingFunction(@PathVariable UUID projectId,
      @PathVariable int year,
      @PathVariable int month) {
    return report1PortIn.downloadPdfFileUsingFunction(projectId, year, month);
  }

}
