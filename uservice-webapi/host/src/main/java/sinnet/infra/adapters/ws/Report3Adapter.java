package sinnet.infra.adapters.ws;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.Report3PortIn;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report3Adapter {

  private final Report3PortIn portIn;
  
  @GetMapping(value = "/3/{projectId}", produces = "application/zip")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId) {
    var result = portIn.downloadPdfFile(projectId);
    return Response.asResponseEntity(result, "report-3.pdf");
       
  }
}
