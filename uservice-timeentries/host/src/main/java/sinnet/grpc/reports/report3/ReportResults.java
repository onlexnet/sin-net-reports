package sinnet.grpc.reports.report3;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import lombok.experimental.UtilityClass;
import sinnet.grpc.reports.common.Fonts;
import sinnet.grpc.reports.common.TableColumn;
import sinnet.grpc.reports.report3.Models.GroupDetails;
import sinnet.grpc.reports.report3.Models.ReportRequest;

@UtilityClass
class ReportResults {

  record ReportResult(ReportRequest request, byte[] content) { }

  record CellParams(String text, TableColumn width, HorizontalAlignment alignment) { }

  private static Font myFont = Fonts.base();
  private static Font myFontBold = Fonts.bold();

  interface AddLine {
    void apply(boolean footerOrHeader, CellParams... params);
  }

  private void asTable(AddLine addLine, List<GroupDetails> activities) {

    for (var item : activities) {
      var personName = item.personName();
      var clients = item.details();

      addLine.apply(true,
        new CellParams(personName, TableColumns.col1widthDescription, HorizontalAlignment.LEFT),
        new CellParams("", TableColumns.col2widthNoted, HorizontalAlignment.CENTER));
      addLine.apply(false,
        new CellParams("Opis", TableColumns.col1widthDescription, HorizontalAlignment.CENTER),
        new CellParams("Notatki", TableColumns.col2widthNoted, HorizontalAlignment.CENTER));

      for (var client : clients) {
        var name = client.name();
        var address = client.address();
        var city = client.city();
        
        var description = name + "\n" + address + "\n"  + city;
        addLine.apply(false,
          new CellParams(description, TableColumns.col1widthDescription, HorizontalAlignment.LEFT),
          new CellParams("", TableColumns.col2widthNoted, HorizontalAlignment.LEFT));
      }
    }

  }
  
  private static PdfPTable newTable() {
    var table = new PdfPTable(TableColumns.width.width());
    var maxWidthPercentage = 100;
    table.setWidthPercentage(maxWidthPercentage);
    return table;
  }

  AddLine addLine(PdfPTable table) {
    return (footerOrHeader, params) -> {
      for (var it : params) {
        var font = !footerOrHeader ? myFont : myFontBold;
         
        var p = new Paragraph(it.text(), font);
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

  static class TableColumnsDef {
    TableColumn col1widthDescription = new TableColumn(3);
    TableColumn col2widthNoted = new TableColumn(7);
    TableColumn width = TableColumn.of(col1widthDescription, col2widthNoted);
  }

  private static TableColumnsDef TableColumns = new TableColumnsDef();

  ReportResult apply(ReportRequest request) {
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

}


