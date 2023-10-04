package sinnet.grpc.reports.report1;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import io.vavr.collection.Iterator;
import io.vavr.control.Option;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import sinnet.grpc.reports.common.Fonts;
import sinnet.grpc.reports.common.TableColumn;
import sinnet.grpc.reports.report1.Models.ActivityDetails;
import sinnet.grpc.reports.report1.Models.ReportRequest;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;

class ReportResults {

  static Font myFont = Fonts.base();
  static Font myFontBold = Fonts.bold();

  interface AddLine {
    void apply(boolean footerOrHeader, CellParams... params);
  }

  static AddLine addLine(PdfPTable table) {
    return (boolean footerOrHeader, CellParams... params) -> {
      for (var it : params) {
        var font = !footerOrHeader ? myFont : myFontBold;
        var adjustedFont = Fonts.adjust(font, it.sizeAdjustment);
        val p = new Paragraph(it.text, adjustedFont);
        
        var cell = new PdfPCell(p);
        if (footerOrHeader) {
          cell.setBorderWidth(0);
        }
        cell.setHorizontalAlignment(it.alignment.getId());
        cell.setColspan(it.width.width());
        table.addCell(cell);
      }
    };
  }

  private static PdfPTable newTable() {
    val table = new PdfPTable(TableColumns.width);
    val maxWidthPercentage = 100;
    table.setWidthPercentage(maxWidthPercentage);
    return table;
  }

  @SneakyThrows
  static ReportResult apply(ReportRequest request) {
    var customer = request.customer();
    var customerName = Option.some(customer.customerName()).getOrElse("(Brak wskazanego kontrahenta)");
    var customerCity = Option.some(customer.customerCity()).getOrElse("(miejscowość)");
    var customerAddress = Option.some(customer.address()).getOrElse("(adres)");

    @Cleanup
    var os = new ByteArrayOutputStream();
    var document = new Document();
    var pdfWriter = PdfWriter.getInstance(document, os);

    document.open();

    var headParam = new Paragraph();
    headParam.add(new Chunk(customerName, myFontBold));
    var header = ", " + customerCity + " ul. " + customerAddress;
    headParam.add(new Chunk(header, myFont));
    document.add(headParam);

    var newLineParagraph = new Paragraph("\n");
    document.add(newLineParagraph);

    var noTime = ActionDuration.empty();
    var partitionedByTime = Iterator.ofAll(request.activities()).partition(it -> !it.howLongInMins().equals(noTime));
    var timedActivities = partitionedByTime._1;

    var table1 = newTable();
    asTable(addLine(table1), timedActivities.toJavaList());
    document.add(table1);
    document.add(newLineParagraph);

    var table2 = newTable();
    var noTimedActivities = partitionedByTime._2;
    tableSpecial(addLine(table2), noTimedActivities.toJavaList());
    document.add(table2);
    document.add(newLineParagraph);

    // We have to invoke close method so that content of the document is written
    // to os and can be obtained as the result of the whole operation
    document.close();

    var content = os.toByteArray();

    return new ReportResult(request, content);

  }

  static TableColumnsDef TableColumns = new TableColumnsDef();

  static class TableColumnsDef {
    TableColumn col1widthServiceman = new TableColumn(50);
    TableColumn col2widthDay = new TableColumn(25);
    TableColumn col3widthDescription = new TableColumn(120);
    TableColumn col4widthDuration = new TableColumn(15);
    TableColumn col5widthDistance = new TableColumn(15);
    int width = col1widthServiceman.width()
              + col2widthDay.width()
              + col3widthDescription.width()
              + col4widthDuration.width()
              + col5widthDistance.width();
  }

  record CellParams(String text, TableColumn width, HorizontalAlignment alignment, OptionalInt sizeAdjustment) {

    CellParams(String text, TableColumn width, HorizontalAlignment alignment) {
      this(text, width, alignment, OptionalInt.empty());
    }
  }


  // import java.time.format.DateTimeFormatter
  static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  static String asString(Optional<LocalDate> value) {
    return value.isPresent()
      ? value.get().format(timeFormatter)
      : "-";
  }

  // Serviceman has sometimes too long name to fit to one line, so minimizing font fits to todays data
  static OptionalInt servicemanNameSizeAdjustment = OptionalInt.of(-4);

  private static void asTable(AddLine addValue, List<ActivityDetails> activities) {

    addValue.apply(false,
      new CellParams("Serwisant", TableColumns.col1widthServiceman, HorizontalAlignment.CENTER),
      new CellParams("Dzień", TableColumns.col2widthDay, HorizontalAlignment.CENTER),
      new CellParams("Praca wykonana", TableColumns.col3widthDescription, HorizontalAlignment.CENTER),
      new CellParams("Czas", TableColumns.col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams("KM", TableColumns.col5widthDistance, HorizontalAlignment.RIGHT));

    for (var item : activities) {
      val howLong = item.howLongInMins();
      var distance = item.howFarInKms();
      var who = item.who();
      addValue.apply(false,
        new CellParams(who, TableColumns.col1widthServiceman, HorizontalAlignment.LEFT, servicemanNameSizeAdjustment),
        new CellParams(asString(item.when()), TableColumns.col2widthDay, HorizontalAlignment.CENTER),
        new CellParams(item.description(), TableColumns.col3widthDescription, HorizontalAlignment.CENTER),
        new CellParams(howLong.asString(), TableColumns.col4widthDuration, HorizontalAlignment.RIGHT),
        new CellParams(distance.toString(), TableColumns.col5widthDistance, HorizontalAlignment.RIGHT));
    }

    record Summary(Distance distance, ActionDuration duration) {

      Summary add(ActivityDetails activity) {
        var distance = Distance.add(this.distance, activity.howFarInKms());
        var duration = ActionDuration.add(this.duration, activity.howLongInMins());
        return new Summary(distance, duration);
      }

    }

    var initialAcc = new Summary(Distance.empty(), ActionDuration.empty());
    var howFarAndHowLong = Iterator.ofAll(activities).foldLeft(initialAcc, (agg, item) -> agg.add(item));
    var howFar = howFarAndHowLong.distance();
    var howLong = howFarAndHowLong.duration();

    addValue.apply(true,
      new CellParams(null, TableColumns.col1widthServiceman, HorizontalAlignment.LEFT),
      new CellParams(null, TableColumns.col2widthDay, HorizontalAlignment.LEFT),
      new CellParams("Suma", TableColumns.col3widthDescription, HorizontalAlignment.RIGHT),
      new CellParams(howLong.asString(), TableColumns.col4widthDuration, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString(), TableColumns.col5widthDistance, HorizontalAlignment.RIGHT));

  }

  private static void tableSpecial(AddLine addValue, List<ActivityDetails> activities) {

    val col1 = TableColumns.col1widthServiceman;
    val col2 = TableColumns.col2widthDay;
    val col3 = TableColumns.col3widthDescription;
    val col4 = TableColumns.col4widthDuration;
    val col5 = TableColumns.col5widthDistance;
    val cols = TableColumn.of(col1, col2, col3, col4, col5);

    addValue.apply(true,
        new CellParams("USŁUGI DODATKOWE", cols, HorizontalAlignment.LEFT));

    addValue.apply(false,
        new CellParams("Serwisant", col1, HorizontalAlignment.CENTER),
        new CellParams("Dzień", col2, HorizontalAlignment.CENTER),
        new CellParams("Praca wykonana", col3, HorizontalAlignment.CENTER),
        new CellParams("Czas", col4, HorizontalAlignment.CENTER),
        new CellParams("KM", col5, HorizontalAlignment.RIGHT));

    for (var item : activities) {
      var distance = item.howFarInKms();
      var who = item.who();
      addValue.apply(false,
        new CellParams(who, col1, HorizontalAlignment.LEFT, servicemanNameSizeAdjustment),
        new CellParams(asString(item.when()), col2, HorizontalAlignment.CENTER),
        new CellParams(item.description(), col3, HorizontalAlignment.CENTER),
        new CellParams("0:00", col4, HorizontalAlignment.RIGHT),
        new CellParams(distance.toString(), col5, HorizontalAlignment.RIGHT));
    }

    val initialAcc = Distance.empty();
    val howFar = Iterator.ofAll(activities).foldLeft(initialAcc, (acc, v) -> Distance.add(acc, v.howFarInKms()));

    addValue.apply(true,
      new CellParams(null, col1, HorizontalAlignment.LEFT),
      new CellParams(null, col2, HorizontalAlignment.LEFT),
      new CellParams("Suma", col3, HorizontalAlignment.RIGHT),
      new CellParams("0:00", col4, HorizontalAlignment.RIGHT),
      new CellParams(howFar.toString(), col5, HorizontalAlignment.RIGHT));

  }

  record ReportResult(ReportRequest request, byte[] content) { }

}

