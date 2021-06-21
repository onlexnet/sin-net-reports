package sinnet

import scala.collection.JavaConversions._

import scala.language.implicitConversions
import javax.inject.Singleton;
import sinnet.reports.ReportsGrpc
import io.grpc.stub.StreamObserver
import scala.util.Try

import java.time.LocalDate
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
import java.util.zip.ZipOutputStream

import scala.collection.JavaConverters._
import java.util.UUID
import java.util.zip.ZipEntry
import com.lowagie.text.pdf.PdfTable
import java.time.format.DateTimeFormatter

object ReportModel {
  def apply(request: ReportRequest): ReportModel = {
    val customer = request.customer
    val customerName = Option(customer.customerName).getOrElse("(Brak wskazanego kontrahenta")
    val customerCity = Option(customer.customerCity).getOrElse("(miejscowość)")
    val customerAddress = Option(customer.address).getOrElse("(adres)")

    val fontSize = 10
    val baseFont = new Font(Font.TIMES_ROMAN, fontSize, Font.NORMAL)

    val d = managed(new ByteArrayOutputStream()) map { os =>
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

      case class CellParams(
          text: String,
          width: Int,
          alignment: HorizontalAlignment
      )

      table.addValue(new CellParams("Serwisant", col1width, HorizontalAlignment.CENTER))
      table.addValue(new CellParams("Dzień", col2width, HorizontalAlignment.CENTER))
      table.addValue(new CellParams("Praca wykonana", col3width, HorizontalAlignment.CENTER))
      table.addValue(new CellParams("Czas", col4width, HorizontalAlignment.RIGHT))
      table.addValue(new CellParams("KM", col5width, HorizontalAlignment.RIGHT))

      val timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
      for (item <- request.details) {
        val howLong = item.howLongInMins
        var distance = item.howFarInKms
        var who = item.who
        table.addValue(new CellParams(who, col1width, HorizontalAlignment.LEFT))
        table.addValue(new CellParams(item.when.format(timeFormatter), col2width, HorizontalAlignment.LEFT))
        table.addValue(new CellParams(item.description, col3width, HorizontalAlignment.LEFT))
        table.addValue(new CellParams(howLong.toString(), col4width, HorizontalAlignment.RIGHT))
        table.addValue(new CellParams(distance.toString(), col5width, HorizontalAlignment.RIGHT))

        // sumTime += howLong.getValue();
        // sumDistance += distance.getValue();
      }

      val agg = (new Kilometers(0), new Minutes(0))
      val (howFar, howLong) = request.details.foldLeft(agg)((acc, v) => (acc._1 + v.howFarInKms, acc._2 + v.howLongInMins))

      // We have to invoke close method so that content of the document is written
      // to os and can be obtained as the result of the whole operation
      document.close()

      os.toByteArray()
    }

    val content = d.opt match {
      case Some(value) => value
      case None        => Array.emptyByteArray
    }

    new ReportModel(request, content)

//       addValue.accept(new CellParams(null, col1width, HorizontalAlignment.LEFT));
//       addValue.accept(new CellParams(null, col2width, HorizontalAlignment.LEFT));
//       addValue.accept(new CellParams("Suma", col3width, HorizontalAlignment.RIGHT));
//       addValue.accept(new CellParams(ActionDuration.of(sumTime).toString(), col4width, HorizontalAlignment.RIGHT));
//       addValue.accept(new CellParams(Distance.of(sumDistance).toString(), col5width, HorizontalAlignment.RIGHT));
//       document.add(table);
//       }

//     return Optional.of(os.toByteArray());

  }
}

case class ReportModel(val request: ReportRequest, val content: Array[Byte])
