package sinnet.adapters.ws;

import java.time.YearMonth;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import sinnet.app.ports.in.Report2PortIn;

@RestController
@RequestMapping("/api/raporty")
@RequiredArgsConstructor
class Report2Adapter {

  private final Report2PortIn report2PortIn;

  @GetMapping("/2/{projectId}")
  public ResponseEntity<byte[]> downloadPdfFile(@PathVariable UUID projectId,
      @RequestParam("yearFrom") int yearFrom, @RequestParam("monthFrom") int monthFrom,
      @RequestParam("yearTo") int yearTo, @RequestParam("monthTo") int monthTo) {

    var from = YearMonth.of(yearFrom, monthFrom);
    var to = YearMonth.of(yearTo, monthTo);
    var result = report2PortIn.downloadPdfFile(projectId,from, to);

    return Response.asResponseEntity(result, "report " + yearFrom + "-" + monthFrom + ".pdf");
  }

}
