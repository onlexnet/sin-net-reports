package net.respekto;

import java.io.ByteArrayOutputStream;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTrigger-Java
     * 2. curl {your host}/api/HttpTrigger-Java?name=HTTP%20Query
     */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage HttpTriggerJava(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter 
        val query = request.getQueryParameters().get("name");
        val name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("invoice-attachment")
    @SneakyThrows
    public HttpResponseMessage invoiceAttachment(
        @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request) {
        

        val output = InvoiceAttachmentFactory.ToContent(new InvoiceAttachmentModel("test"));

        val response = request.createResponseBuilder(HttpStatus.OK)
            .header("content-type", "application/pdf")
            .header("Content-Disposition", "inline; filename=report.pdf")
            .body(output)
            .build();

        return response;
    }
}
