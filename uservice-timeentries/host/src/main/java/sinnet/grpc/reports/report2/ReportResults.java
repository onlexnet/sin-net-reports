package sinnet.grpc.reports.report2;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.val;
import lombok.experimental.UtilityClass;
import sinnet.grpc.reports.common.Fonts;
import sinnet.grpc.reports.common.TableColumn;
import sinnet.grpc.reports.report2.Models.ActivityDetails;
import sinnet.grpc.reports.report2.Models.ReportRequest;

@UtilityClass
class ReportResults {

  private static Font myFont = Fonts.base();
  private static Font myFontBold = Fonts.bold();

  record ReportResult(ReportRequest request, byte[] content) { }

  record CellParams(String text, TableColumn width, HorizontalAlignment alignment) { }

  interface AddLine {
    void apply(boolean footerOrHeader, CellParams... params);
  }

  static AddLine addLine(PdfPTable table) {
    return (footerOrHeader, params) -> {
      for (var it : params) {
        var font = !footerOrHeader ? myFont : myFontBold;
        var p = new Paragraph(it.text, font);
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

  private static void asTable(AddLine addLine, List<ActivityDetails> activities) {

    addLine.apply(false,
      new CellParams("Kiedy", TableColumns.col1widthPeriod, HorizontalAlignment.CENTER),
      new CellParams("Opis", TableColumns.col2widthPerson, HorizontalAlignment.CENTER),
      new CellParams("Czas", TableColumns.col3widthHours, HorizontalAlignment.CENTER),
      new CellParams("Km", TableColumns.col4widthKms, HorizontalAlignment.CENTER));

    for (var item : activities) {
      var period = item.period();
      var description = item.personName();
      var minutes = item.minutes();
      var kilometers = item.kilometers(); 
      addLine.apply(false,
        new CellParams(period.toString(), TableColumns.col1widthPeriod, HorizontalAlignment.LEFT),
        new CellParams(description.toString(), TableColumns.col2widthPerson, HorizontalAlignment.LEFT),
        new CellParams(minutes.asString(), TableColumns.col3widthHours, HorizontalAlignment.RIGHT),
        new CellParams(kilometers.toString(), TableColumns.col4widthKms, HorizontalAlignment.RIGHT));
    }
  }

  private static PdfPTable newTable() {
    val table = new PdfPTable(TableColumns.width);
    val maxWidthPercentage = 100;
    table.setWidthPercentage(maxWidthPercentage);
    return table;
  }

  static ReportResult apply(ReportRequest request) {
    var os = new ByteArrayOutputStream();
    var document = new Document();
    var pdfWriter = PdfWriter.getInstance(document, os);

    document.open();

    var table1 = newTable();
    asTable(addLine(table1), request.activities());
    document.add(table1);

    // We have to invoke close method so that content of the document is written
    // to os and can be obtained as the result of the whole operation
    document.close();
    var content = os.toByteArray();

    return new ReportResult(request, content);
  }

  static TableColumnsDef TableColumns = new TableColumnsDef();

  static class TableColumnsDef {
    TableColumn col1widthPeriod = new TableColumn(2);
    TableColumn col2widthPerson = new TableColumn(10);
    TableColumn col3widthHours = new TableColumn(2);
    TableColumn col4widthKms = new TableColumn(2);
    int width = col1widthPeriod.width() + col2widthPerson.width() + col3widthHours.width() + col4widthKms.width();
  }

}
