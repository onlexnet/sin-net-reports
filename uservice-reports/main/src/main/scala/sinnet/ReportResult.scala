package sinnet

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

object ReportResult {

  def apply(request: ReportRequest): ReportResult = {
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

      def asTable(activities: Seq[ActivityDetails]): PdfPTable = {
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
  
        val timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        implicit val a: (Option[LocalDate]) => String = it =>
          it match {
            case Some(date) => date.format(timeFormatter)
            case None       => "-"
          }

        table.addValue(new CellParams("Serwisant", col1width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Dzień", col2width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Praca wykonana", col3width, HorizontalAlignment.CENTER))
        table.addValue(new CellParams("Czas", col4width, HorizontalAlignment.RIGHT))
        table.addValue(new CellParams("KM", col5width, HorizontalAlignment.RIGHT))
          
        for (item <- request.details) {
          val howLong = item.howLongInMins
          var distance = item.howFarInKms
          var who = item.who
          table.addValue(new CellParams(who, col1width, HorizontalAlignment.LEFT))
          table.addValue(new CellParams(item.when, col2width, HorizontalAlignment.LEFT))
          table.addValue(new CellParams(item.description, col3width, HorizontalAlignment.LEFT))
          table.addValue(new CellParams(howLong.toString(), col4width, HorizontalAlignment.RIGHT))
          table.addValue(new CellParams(distance.toString(), col5width, HorizontalAlignment.RIGHT))
        }

        val initialAcc = (Kilometers(0), Minutes(0))
        val (howFar, howLong) = request.details.foldLeft(initialAcc)((acc, v) => (acc._1 + v.howFarInKms, acc._2 + v.howLongInMins))

        table.addValue(new CellParams(null, col1width, HorizontalAlignment.LEFT))
        table.addValue(new CellParams(null, col2width, HorizontalAlignment.LEFT))
        table.addValue(new CellParams("Suma", col3width, HorizontalAlignment.RIGHT))
        table.addValue(new CellParams(howLong.toString(), col4width, HorizontalAlignment.RIGHT))
        table.addValue(new CellParams(howFar.toString(), col5width, HorizontalAlignment.RIGHT))

        table
      }

      document.add(asTable(request.details));

      // We have to invoke close method so that content of the document is written
      // to os and can be obtained as the result of the whole operation
      document.close()

      os.toByteArray()
    }

    val content = d.opt match {
      case Some(value) => value
      case None        => Array.emptyByteArray
    }

    new ReportResult(request, content)

  }

}

case class ReportModel()
case class ReportResult(val request: ReportRequest, val content: Array[Byte])