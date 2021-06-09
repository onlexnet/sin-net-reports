package sinnet

import javax.inject.Singleton;
import sinnet.reports.ReportsGrpc
import io.grpc.stub.StreamObserver
import sinnet.reports.{ReportRequest, Response}
import scala.util.Try

import resource._

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import com.google.protobuf.ByteString
import io.grpc.Deadline
import io.grpc.Context
import sinnet.reports.ReportRequests

@Singleton
class ReportService extends ReportsGrpc.ReportsImplBase {
  override def produce(request: ReportRequest, responseObserver: StreamObserver[Response]): Unit = {

    var customer = request.getCustomer()
    var customerName = Option(customer.getCustomerName()).getOrElse("Brak przypisanego kontrahenta")
    var customerCity = Option(customer.getCustomerCity()).getOrElse("")
    var customerAddress = Option(customer.getCustomerAddress()).getOrElse("")

    val fontSize = 10
    val baseFont = new Font(Font.TIMES_ROMAN, fontSize, Font.NORMAL)

    val header = s"$customerName $customerCity $customerAddress"
    val headParam = new Paragraph(header, baseFont)

    for (os <- managed(new ByteArrayOutputStream())) {

      val document = new Document()
      val pdfWriter = PdfWriter.getInstance(document, os)
      document.open()
      document.add(headParam);
      document.add(new Paragraph("-"))

      // We have to invoke close method so that content of the document is written
      // to os and can be obtained as the result of the whole operation
      document.close()
      val result = os.toByteArray()

      val data = ByteString.copyFrom(result)
      val response = Response.newBuilder()
        .setData(data)
        .build()
      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }
  }

  override def producePack(request: ReportRequests, responseObserver: StreamObserver[Response]): Unit = {
    
  }

  // def getAsZip(items: Array[ListItem]): Array[Byte] {
  // }
  // //   @Cleanup
  //   var baos = new ByteArrayOutputStream();
  //   {
  //       var customers = items.groupBy(it -> it.getValue().getWhom());

  //       @Cleanup
  //       var zos = new ZipOutputStream(baos);

  //       for (var c : customers) {
  //           var customerName = c._2.head().getCustomerName();
  //           var entry = new ZipEntry(customerName + ".pdf");
  //           var itemsForCustomer = c._2;
  //           var o = produceReport(itemsForCustomer);
  //           if (!o.isPresent()) continue;
  //           zos.putNextEntry(entry);
  //           zos.write(o.get());
  //           zos.closeEntry();
  //       }

  //   }

  //   return baos.toByteArray();

  // }
}
