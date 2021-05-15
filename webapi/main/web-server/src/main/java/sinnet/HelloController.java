package sinnet;

import java.security.Principal;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.grpc.ManagedChannelBuilder;
import io.quarkus.example.GreeterGrpc;
import io.quarkus.example.HelloReply;
import io.quarkus.example.HelloRequest;

/** Test controller to find connection from webapi to backend service through gRPC api. */
@RestController
@RequestMapping("/debug")
public class HelloController {

  private GreeterGrpc.GreeterBlockingStub client;

  @PostConstruct
  public void init() {
    var channel = ManagedChannelBuilder.forAddress("localhost", 9000)
        .usePlaintext()
        .build();
    this.client = GreeterGrpc.newBlockingStub(channel);
  }

  @RequestMapping(value = "/hello", method = RequestMethod.GET)
  @ResponseBody
  public String helloWorld(@RequestParam(defaultValue = "no-name") String name) {
    var request = HelloRequest.newBuilder().setName(name).build();
    var response = client.sayHello(request);
    return response.getMessage();
  }
}

