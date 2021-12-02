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
      // document.add(newLineParagraph)

      // var table2 = newTable()
      // tableSpecial(addLine(table2), noTimedActivities)
      // document.add(table2);
      // document.add(newLineParagraph)

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
    def Col1widthServiceman = new TableColumn(3)
    def Col2widthDay = new TableColumn(3)
    def Col3widthDescription = new TableColumn(12)
    def Col4widthDuration = new TableColumn(2)
    def Col5widthDistance = new TableColumn(2)
    def width = Col1widthServiceman.width +
                Col2widthDay.width +
                Col3widthDescription.width +
                Col4widthDuration.width +
                Col5widthDistance.width
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
      new CellParams("Serwisant1", TableColumns.Col1widthServiceman, HorizontalAlignment.CENTER),
      new CellParams("Dzień1", TableColumns.Col2widthDay, HorizontalAlignment.CENTER),
      new CellParams("Praca wykonana1", TableColumns.Col3widthDescription, HorizontalAlignment.CENTER),
      new CellParams("Czas1", TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams("KM1", TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))

    for (item <- activities) {
      val howLong = 42
      var distance = 42
      var who = "Aaaaa"
      addValue(false,
        new CellParams(who, TableColumns.Col1widthServiceman, HorizontalAlignment.LEFT),
        new CellParams(item.personName, TableColumns.Col2widthDay, HorizontalAlignment.LEFT),
        new CellParams(item.personName, TableColumns.Col3widthDescription, HorizontalAlignment.LEFT),
        new CellParams(howLong.toString(), TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
        new CellParams(distance.toString(), TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))
    }

    val initialAcc = (Kilometers(0), Minutes(0))
    val (howFar, howLong) = activities.foldLeft(initialAcc)((acc, v) => (acc._1 + v.kilometers, acc._2 + v.minutes))

    addValue(true,
      new CellParams(null, TableColumns.Col1widthServiceman, HorizontalAlignment.LEFT),
      new CellParams(null, TableColumns.Col2widthDay, HorizontalAlignment.LEFT),
      new CellParams("Suma", TableColumns.Col3widthDescription, HorizontalAlignment.RIGHT),
      new CellParams(howLong.toString(), TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString(), TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))

  }
}

case class ReportModel()
case class ReportResult(val request: ReportRequest, val content: Array[Byte])

