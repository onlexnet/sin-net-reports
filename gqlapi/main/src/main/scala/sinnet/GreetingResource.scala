package sinnet

import javax.ws.rs.{GET, Path, Produces}
import javax.ws.rs.core.MediaType
import javax.inject.Inject
import io.quarkus.example.GreeterGrpc
import io.quarkus.grpc.runtime.annotations.GrpcService

@Path("/hello-resteasy")
class GreetingResource {

    @Inject
    @GrpcService("hello")
    var client: GreeterGrpc.GreeterFutureStub = _


    @GET
    @Produces(Array[String](MediaType.TEXT_PLAIN))
    def hello() = "Hello RESTEasy"
}