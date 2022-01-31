package sinnet.web;

import java.util.concurrent.CompletionStage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import lombok.extern.slf4j.Slf4j;
import sinnet.FutureExecutor;
import sinnet.report1.grpc.ActivityDetails;
import sinnet.report1.grpc.CustomerDetails;
import sinnet.report1.grpc.ReportRequest;
import sinnet.report1.grpc.ReportRequests;
import sinnet.report1.grpc.ReportsGrpc.ReportsFutureStub;
import sinnet.reports.grpc.Date;

/**
 * Test controller to test calls endpoint -> webapi -> backend service using gRPC api.
 *
 * <p>We use Apache benchmark tool (ab) for simple performance tests. (sudo apt install apache2-utils)
 * Test webapi response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-webapi?name=asia
 * Test service response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-service?name=asia
 */
@RestController
@RequestMapping("/api/debug")
@Slf4j
class DebugController {

  @Autowired
  private GreeterGrpc.GreeterFutureStub client;

  @Autowired
  private ReportsFutureStub reportsClient;

  @Autowired
  private FutureExecutor executor;

  @RequestMapping(value = "/hello-by-service", method = RequestMethod.GET)
  @ResponseBody
  public CompletionStage<String> helloByService(@RequestParam(defaultValue = "no-name") String name) {
    var request = HelloRequest.newBuilder().setName(name).build();
    var response = client.sayHello(request);
    return executor.asFuture(response, it -> it.getMessage());
  }

  @RequestMapping(value = "/hello-by-webapi", method = RequestMethod.GET)
  @ResponseBody
  public String helloByWebapi(@RequestParam(defaultValue = "no-name") String name) {
    return "Hello from WebApi, " + name;
  }

  @RequestMapping(value = "/report-pdf", method = RequestMethod.GET, produces = "application/pdf")
  public CompletionStage<ResponseEntity<byte[]>> downloadPdfFile() {
    var headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "inline; filename=test-report.pdf");
    headers.add("Expires", "0");

    var response = reportsClient.produce(generateRequest(0));
    return executor.asFuture(response, it -> {
      var result = it.getData().toByteArray();
      log.info("Raport: rozmiar:{}", result.length);
      return ResponseEntity.ok()
        .headers(headers)
        .contentLength(result.length)
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(result);
    });
  }

  @RequestMapping(value = "/report-zip", method = RequestMethod.GET, produces = "application/zip")
  public CompletionStage<ResponseEntity<byte[]>> downloadZipFile() {
    var headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "inline; filename=test-report.zip");
    headers.add("Expires", "0");

    var request1 = generateRequest(1);
    var request2 = generateRequest(2);
    var requests = ReportRequests.newBuilder()
        .addItems(request1)
        .addItems(request2)
        .build();
    var response = reportsClient.producePack(requests);
    return executor.asFuture(response, it -> {
      var result = it.getData().toByteArray();
      log.info("Raport: rozmiar:{}", result.length);
      return ResponseEntity.ok()
        .headers(headers)
        .contentLength(result.length)
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(result);
    });
  }

  private static ReportRequest generateRequest(int no) {
    var customMarker = " [" + no + "]";
    var customer = CustomerDetails.newBuilder()
        .setCustomerAddress("Adres" + customMarker)
        .setCustomerCity("Miasto" + customMarker)
        .setCustomerId("ID klienta" + customMarker)
        .setCustomerName("Nazwa klienta" + customMarker)
        .build();
    var action = ActivityDetails.newBuilder()
        .setDescription("Opis usługi" + customMarker)
        .setHowFarInKms(1)
        .setHowLongInMins(1)
        .setWho("Some serviceman")
        .setWhen(Date.newBuilder()
          .setYear(2000)
          .setMonth(1)
          .setDayOfTheMonth(2))
        .build();
    return ReportRequest.newBuilder()
        .setCustomer(customer)
        .addDetails(action)
        .build();
  }
}

