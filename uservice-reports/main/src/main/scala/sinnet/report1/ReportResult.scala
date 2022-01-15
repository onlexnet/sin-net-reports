package sinnet.report1

import scala.language.implicitConversions
import javax.inject.Singleton;
import sinnet.report1.grpc.ReportsGrpc
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
    val customer = request.customer
    val customerName = Option(customer.customerName).getOrElse("(Brak wskazanego kontrahenta)")
    val customerCity = Option(customer.customerCity).getOrElse("(miejscowość)")
    val customerAddress = Option(customer.address).getOrElse("(adres)")

    val d = managed(new ByteArrayOutputStream()) map { os =>
      val document = new Document()
      val pdfWriter = PdfWriter.getInstance(document, os)

      document.open()

      val header = s"$customerName, $customerCity ul. $customerAddress"
      val headParam = new Paragraph(header, myFont)

      val newLineParagraph = new Paragraph("\n")
      document.add(headParam)
      document.add(newLineParagraph)

      def addLine(table: PdfPTable)(footerOrHeader: Boolean, v: CellParams*): Unit = {
        for (it <- v) {
          val font = (if (!footerOrHeader) myFont else myFontBold)
          var adjustedFont = Fonts.adjust(font, it.sizeAdjustment)
          val p = new Paragraph(it.text, adjustedFont)
          
          var cell = new PdfPCell(p)
          if (footerOrHeader) {
            cell.setBorderWidth(0)
          }
          cell.setHorizontalAlignment(it.alignment.getId())
          cell.setColspan(it.width.width)
          table.addCell(cell)
        }
      }

      val noTime = Minutes(0)
      val (timedActivities, noTimedActivities) = request.activities.partition(_.howLongInMins != noTime)

      var table1 = newTable()
      asTable(addLine(table1), timedActivities)
      document.add(table1);
      document.add(newLineParagraph)

      var table2 = newTable()
      tableSpecial(addLine(table2), noTimedActivities)
      document.add(table2);
      document.add(newLineParagraph)

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
    def Col1widthServiceman = new TableColumn(50)
    def Col2widthDay = new TableColumn(25)
    def Col3widthDescription = new TableColumn(120)
    def Col4widthDuration = new TableColumn(15)
    def Col5widthDistance = new TableColumn(15)
    def width = Col1widthServiceman.width +
                Col2widthDay.width +
                Col3widthDescription.width +
                Col4widthDuration.width +
                Col5widthDistance.width
  }

  case class CellParams(text: String, width: TableColumn, alignment: HorizontalAlignment, sizeAdjustment: Option[Int] = None)

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

  // Serviceman has sometimes too long name to fit to one line, so minimizing font fits to todays data
  val servicemanNameSizeAdjustment = Some(-4)

  private def asTable(addValue: (Boolean, CellParams*) => Unit, activities: Seq[ActivityDetails]): Unit = {

    addValue(false,
      new CellParams("Serwisant", TableColumns.Col1widthServiceman, HorizontalAlignment.CENTER),
      new CellParams("Dzień", TableColumns.Col2widthDay, HorizontalAlignment.CENTER),
      new CellParams("Praca wykonana", TableColumns.Col3widthDescription, HorizontalAlignment.CENTER),
      new CellParams("Czas", TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams("KM", TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))

    for (item <- activities) {
      val howLong = item.howLongInMins
      var distance = item.howFarInKms
      var who = item.who
      addValue(false,
        new CellParams(who, TableColumns.Col1widthServiceman, HorizontalAlignment.LEFT, servicemanNameSizeAdjustment),
        new CellParams(item.when, TableColumns.Col2widthDay, HorizontalAlignment.LEFT),
        new CellParams(item.description, TableColumns.Col3widthDescription, HorizontalAlignment.LEFT),
        new CellParams(howLong.asString, TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
        new CellParams(distance.toString(), TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))
    }

    val initialAcc = (Kilometers(0), Minutes(0))
    val (howFar, howLong) = activities.foldLeft(initialAcc)((acc, v) => (acc._1 + v.howFarInKms, acc._2 + v.howLongInMins))

    addValue(true,
      new CellParams(null, TableColumns.Col1widthServiceman, HorizontalAlignment.LEFT),
      new CellParams(null, TableColumns.Col2widthDay, HorizontalAlignment.LEFT),
      new CellParams("Suma", TableColumns.Col3widthDescription, HorizontalAlignment.RIGHT),
      new CellParams(howLong.asString, TableColumns.Col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString(), TableColumns.Col5widthDistance, HorizontalAlignment.RIGHT))

  }

  private def tableSpecial(addValue: (Boolean, CellParams*) => Unit, activities: Seq[ActivityDetails]): Unit = {

    val col1 = TableColumns.Col1widthServiceman
    val col2 = TableColumns.Col2widthDay
    val col3 = TableColumns.Col3widthDescription
    val col4 = TableColumns.Col4widthDuration
    val col5 = TableColumns.Col5widthDistance

    addValue(false,
        new CellParams("Serwisant", col1, HorizontalAlignment.CENTER),
        new CellParams("Dzień", col2, HorizontalAlignment.CENTER),
        new CellParams("Praca wykonana", col3, HorizontalAlignment.CENTER),
        new CellParams("Czas", col4, HorizontalAlignment.CENTER),
        new CellParams("KM", col5, HorizontalAlignment.RIGHT))

    for (item <- activities) {
      val howLong = item.howLongInMins
      var distance = item.howFarInKms
      var who = item.who
      addValue(false,
        new CellParams(who, col1, HorizontalAlignment.LEFT, servicemanNameSizeAdjustment),
        new CellParams(item.when, col2, HorizontalAlignment.LEFT),
        new CellParams(item.description, col3, HorizontalAlignment.LEFT),
        new CellParams("0:00", col4, HorizontalAlignment.CENTER),
        new CellParams(distance.toString(), col5, HorizontalAlignment.RIGHT))
    }

    val initialAcc = Kilometers(0)
    val howFar = activities.foldLeft(initialAcc)((acc, v) => (acc + v.howFarInKms))

    addValue(true,
      new CellParams(null, col1, HorizontalAlignment.LEFT),
      new CellParams(null, col2, HorizontalAlignment.LEFT),
      new CellParams("Suma", col3, HorizontalAlignment.RIGHT),
      new CellParams("0:00", col4, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString(), col5, HorizontalAlignment.RIGHT))

  }
}

case class ReportModel()
case class ReportResult(val request: ReportRequest, val content: Array[Byte])
