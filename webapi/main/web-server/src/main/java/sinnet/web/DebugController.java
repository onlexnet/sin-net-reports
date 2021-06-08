package sinnet.web;

import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.grpc.ManagedChannelBuilder;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import sinnet.FutureExecutor;
import sinnet.reports.ActionDetails;
import sinnet.reports.CustomerDetails;
import sinnet.reports.ReportRequest;
import sinnet.reports.ReportsGrpc;

/**
 * Test controller to test calls endpoint -> webapi -> backend service using gRPC api.
 *
 * <p>We use Apache benchmark tool (ab) for simple performance tests. (sudo apt install apache2-utils)
 * Test webapi response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-webapi?name=asia
 * Test service response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-service?name=asia
 */
@RestController
@RequestMapping("/api/debug")
class DebugController {

  private GreeterGrpc.GreeterFutureStub client;
  private ReportsGrpc.ReportsFutureStub reportsClient;

  @Autowired
  private FutureExecutor executor;

  @PostConstruct
  public void init() {
    var channel = ManagedChannelBuilder.forAddress("uservice-reports", 9000)
        .usePlaintext()
        .build();

    this.client = GreeterGrpc.newFutureStub(channel);
    this.reportsClient = ReportsGrpc.newFutureStub(channel);
  }

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

  @RequestMapping(value = "/report", method = RequestMethod.GET, produces = "application/pdf")
  public CompletionStage<ResponseEntity<byte[]>> downloadPDFFile() {
    var headers = new HttpHeaders();
    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
    headers.add("Content-Disposition", "inline; filename=test-report.zip");
    headers.add("Pragma", "no-cache");
    headers.add("Expires", "0");

    var customer = CustomerDetails.newBuilder()
        .setCustomerAddress("Adres")
        .setCustomerCity("Miasto")
        .setCustomerId("ID klienta")
        .setCustomerName("Nazwa klienta")
        .build();
    var action = ActionDetails.newBuilder()
        .setDescription("Opis usługi")
        .build();
    var request = ReportRequest.newBuilder()
        .setCustomer(customer)
        .addDetails(action)
        .build();
    var response = reportsClient.produce(request);
    return executor.asFuture(response, it -> {
      var result = it.getData().toByteArray();
      return ResponseEntity.ok()
        .headers(headers)
        .contentLength(result.length)
        .contentType(MediaType.parseMediaType("application/octet-stream"))
        .body(result);
    });
  }

}

