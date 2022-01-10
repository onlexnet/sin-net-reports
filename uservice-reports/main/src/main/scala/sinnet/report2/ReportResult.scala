package sinnet.report2

import scala.language.implicitConversions
import javax.inject.Singleton;
import sinnet.report2.grpc.ReportsGrpc
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
import org.librepdf.openpdf.fonts.Liberation
import com.lowagie.text.pdf.BaseFont
import scala.util.Success
import scala.util.Failure
import com.lowagie.text.FontFactory
import javax.inject.Inject
import sinnet.reports._
import sinnet.config.Fonts

object ReportResult {

  val myFont = Fonts.base
  val myFontBold = Fonts.bold

  def apply(request: ReportRequest): ReportResult = {
    val d = managed(new ByteArrayOutputStream()) map { os =>
      val document = new Document()
      val pdfWriter = PdfWriter.getInstance(document, os)

      document.open()

      def addLine(table: PdfPTable)(isFooter: Boolean, v: CellParams*): Unit = {
        for (it <- v) {
          val font = if (!isFooter) myFont else myFontBold
          
          val p = new Paragraph(it.text, font)
          var cell = new PdfPCell(p)
          if (isFooter) {
            cell.setBorderWidth(0)
          }
          cell.setHorizontalAlignment(it.alignment.getId())
          cell.setColspan(it.width.width)
          table.addCell(cell)
        }
      }

      var table1 = newTable()
      asTable(addLine(table1), request.activities)
      document.add(table1);

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

  // List of all columns available for activities.
  // In some logic we can merge some of them (using e.g. sum of two values for one column)
  // but finally we should use all columns so that all tables create using such columns will have
  // similar location of columns.
  case class TableColumn(val width: Int) {
    def +(that: TableColumn) = new TableColumn(this.width + that.width)
  }
  object TableColumns {
    def Col1widthPeriod = new TableColumn(2)
    def Col2widthPerson = new TableColumn(10)
    def Col3widthHours = new TableColumn(2)
    def Col4widthKms = new TableColumn(2)
    def width = Col1widthPeriod.width +
      Col2widthPerson.width +
      Col3widthHours.width +
      Col4widthKms.width
  }

  case class CellParams(text: String, width: TableColumn, alignment: HorizontalAlignment)

  private def newTable(): PdfPTable = {
    val table = new PdfPTable(TableColumns.width)
    val maxWidthPercentage = 100
    table.setWidthPercentage(maxWidthPercentage)
    table
  }

  import java.time.format.DateTimeFormatter
  val timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
  implicit val a: (Option[LocalDate]) => String = it =>
    it match {
      case Some(date) => date.format(timeFormatter)
      case None       => "-"
    }

  private def asTable(addValue: (Boolean, CellParams*) => Unit, activities: Seq[ActivityDetails]): Unit = {

    addValue(false,
      new CellParams("Kiedy", TableColumns.Col1widthPeriod, HorizontalAlignment.CENTER),
      new CellParams("Opis", TableColumns.Col2widthPerson, HorizontalAlignment.CENTER),
      new CellParams("Czas", TableColumns.Col3widthHours, HorizontalAlignment.CENTER),
      new CellParams("Km", TableColumns.Col4widthKms, HorizontalAlignment.CENTER))

    for (item <- activities) {
      val period = item.period;
      val description = item.personName;
      val minutes = item.minutes;
      val kilometers = item.kilometers; 
      addValue(false,
        new CellParams(period.toString, TableColumns.Col1widthPeriod, HorizontalAlignment.LEFT),
        new CellParams(description.toString, TableColumns.Col2widthPerson, HorizontalAlignment.LEFT),
        new CellParams(minutes.asString, TableColumns.Col3widthHours, HorizontalAlignment.RIGHT),
        new CellParams(kilometers.toString(), TableColumns.Col4widthKms, HorizontalAlignment.RIGHT))
    }

    val initialAcc = (Kilometers(0), Minutes(0))
    val (howFar, howLong) = activities.foldLeft(initialAcc)((acc, v) => (acc._1 + v.kilometers, acc._2 + v.minutes))

    addValue(true,
      new CellParams("", TableColumns.Col1widthPeriod, HorizontalAlignment.LEFT),
      new CellParams("Suma", TableColumns.Col2widthPerson, HorizontalAlignment.LEFT),
      new CellParams(howLong.asString, TableColumns.Col3widthHours, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString, TableColumns.Col4widthKms, HorizontalAlignment.RIGHT))

  }
}

case class ReportModel()
case class ReportResult(val request: ReportRequest, val content: Array[Byte])

