package sinnet

import javax.inject.Singleton;
import sinnet.reports.ReportsGrpc
import io.grpc.stub.StreamObserver
import sinnet.reports.{ReportRequest, Response}
import scala.util.Try

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import com.google.protobuf.ByteString

@Singleton
class ReportService extends ReportsGrpc.ReportsImplBase {
  override def produce(request: ReportRequest, responseObserver: StreamObserver[Response]): Unit = {
    var customer = request.getCustomer();
    var customerName = Option(customer.getCustomerName()).getOrElse("Brak przypisanego kontrahenta")
    var customerCity = Option(customer.getCustomerCity()).getOrElse("")
    var customerAddress = Option(customer.getCustomerAddress()).getOrElse("")

    val fontSize = 10
    val baseFont = new Font(Font.TIMES_ROMAN, fontSize, Font.NORMAL)

    var header = s"$customerName $customerCity $customerAddress"
    var headParam = new Paragraph(header, baseFont)

    var os = new ByteArrayOutputStream()
    var document = new Document()
    val pdfWriter = PdfWriter.getInstance(document, os)

    document.open();
    document.add(headParam);
    document.add(new Paragraph("-"))

    val result = os.toByteArray()
    os.close()

    val data = ByteString.copyFrom(result)
    val response = Response.newBuilder()
      .setData(data)
      .build()

  }
}
