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
import java.util.zip.ZipOutputStream

import scala.collection.JavaConverters._
import java.util.UUID
import java.util.zip.ZipEntry
import com.lowagie.text.pdf.PdfTable

@Singleton
class ReportService extends ReportsGrpc.ReportsImplBase {
  override def produce(request: ReportRequest, responseObserver: StreamObserver[Response]): Unit = {
    val binaryData = produceReport(request)
    val dtoData = ByteString.copyFrom(binaryData)
    var response = Response.newBuilder()
        .setData(dtoData)
        .build()

    responseObserver.onNext(response)
    responseObserver.onCompleted()
  }

  def produceReport(request: ReportRequest): Array[Byte] = {
    val customer = request.getCustomer()
    val customerName = Option(customer.getCustomerName()).getOrElse("Brak przypisanego kontrahenta")
    val customerCity = Option(customer.getCustomerCity()).getOrElse("")
    val customerAddress = Option(customer.getCustomerAddress()).getOrElse("")

    val fontSize = 10
    val baseFont = new Font(Font.TIMES_ROMAN, fontSize, Font.NORMAL)

    val d = managed(new ByteArrayOutputStream()) map {
      os => 
        val document = new Document()
        val pdfWriter = PdfWriter.getInstance(document, os)

        document.open()

        val header = s"$customerName $customerCity $customerAddress"
        val headParam = new Paragraph(header, baseFont)

        document.add(headParam);
        document.add(new Paragraph("-"))

        val col1width = 3
        val col2width = 3
        val col3width = 12
        val col4width = 2
        val col5width = 2
        val table = new PdfPTable(col1width + col2width + col3width + col4width + col5width)
        val maxWidthPercentage = 100
        table.setWidthPercentage(maxWidthPercentage)

        implicit class PdfPTableEx(val it: PdfPTable) {
          def addValue(v: CellParams): Unit = {
            val p = new Paragraph(v.text, baseFont)
            var cell = new PdfPCell(p)
            cell.setHorizontalAlignment(v.alignment.getId())
            cell.setColspan(v.width)
            it.addCell(cell)
          }
        }

        case class CellParams(text: String, width: Int, alignment: HorizontalAlignment)

        var sumTime = 0
        var sumDistance = 0
        table.addValue(new CellParams("Serwisant", col1width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Dzień", col2width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Praca wykonana", col3width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Czas", col4width, HorizontalAlignment.RIGHT))
        table.addValue(new CellParams("KM", col5width, HorizontalAlignment.RIGHT))

//       final var timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//       for (var item : items) {
//           var howLong = item.getValue().getHowLong();
//           var distance = item.getValue().getHowFar();
//           var who = Optional
//               .ofNullable(item.getValue().getWho().getValue())
//               .map(it -> it.split("@")[0])
//               .orElse(null);
//           addValue.accept(new CellParams(who, col1width, HorizontalAlignment.LEFT));
//           addValue.accept(new CellParams(item.getValue().getWhen().format(timeFormatter), col2width, HorizontalAlignment.LEFT));
//           addValue.accept(new CellParams(item.getValue().getWhat(), col3width, HorizontalAlignment.LEFT));
//           addValue.accept(new CellParams(howLong.toString(), col4width, HorizontalAlignment.RIGHT));
//           addValue.accept(new CellParams(distance.toString(), col5width, HorizontalAlignment.RIGHT));
//           sumTime += howLong.getValue();
//           sumDistance += distance.getValue();
//       }



        // We have to invoke close method so that content of the document is written
        // to os and can be obtained as the result of the whole operation
        document.close()

        os.toByteArray()
    }

    d.opt match {
      case Some(value) => value
      case None => Array.emptyByteArray
    }
   





//       addValue.accept(new CellParams(null, col1width, HorizontalAlignment.LEFT));
//       addValue.accept(new CellParams(null, col2width, HorizontalAlignment.LEFT));
//       addValue.accept(new CellParams("Suma", col3width, HorizontalAlignment.RIGHT));
//       addValue.accept(new CellParams(ActionDuration.of(sumTime).toString(), col4width, HorizontalAlignment.RIGHT));
//       addValue.accept(new CellParams(Distance.of(sumDistance).toString(), col5width, HorizontalAlignment.RIGHT));
//       document.add(table);
//       }

//     return Optional.of(os.toByteArray());
    
  }

  override def producePack(request: ReportRequests, responseObserver: StreamObserver[Response]): Unit = {
    for (
      baos <- managed(new ByteArrayOutputStream());
      zos <- managed(new ZipOutputStream(baos))) {

      for ( 
        (item, index) <- request.getItemsList().asScala.zip(Stream from 1)) {
        val report = produceReport(item)
        val entry = new ZipEntry(s"$index-${ item.getCustomer.getCustomerName() }.pdf")
        zos.putNextEntry(entry)
        zos.write(report)
        zos.closeEntry()
      }

      zos.close()

      val binaryData = baos.toByteArray()
      val dtoData = ByteString.copyFrom(binaryData)
      var response = Response.newBuilder()
          .setData(dtoData)
          .build()

      responseObserver.onNext(response)
      responseObserver.onCompleted()
    }
  }
}
