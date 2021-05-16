package sinnet.web;

import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;

import com.google.common.util.concurrent.Futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.grpc.ManagedChannelBuilder;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloRequest;
import sinnet.FutureExecutor;

/**
 * Test controller to test calls endpoint -> webapi -> backend service using gRPC api.
 * <p>
 * We use apache benchamrking tool (ab) for simple performance tests. (sudo apt install apache2-utils)
 * Test webapi response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-webapi?name=asia
 * Test service response: ab -n 10 -c 2 https://raport.sin.net.pl/api/debug/hello-by-service?name=asia
 */
@RestController
@RequestMapping("/api/debug")
class DebugController {

  private GreeterGrpc.GreeterFutureStub client;

  @Autowired
  private FutureExecutor executor;

  @PostConstruct
  public void init() {
    var channel = ManagedChannelBuilder.forAddress("uservice-reports", 9000)
        .usePlaintext()
        .build();
    this.client = GreeterGrpc.newFutureStub(channel);
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
}

