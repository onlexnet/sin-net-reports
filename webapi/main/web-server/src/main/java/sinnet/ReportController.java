package sinnet;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.collection.Array;
import lombok.Cleanup;
import lombok.SneakyThrows;
import sinnet.read.ActionProjection;

@RestController
@RequestMapping(path = "/raporty")
public class ReportController implements ActionProjection {

    @Autowired
    private ActionProjection.Provider projection;

    @RequestMapping(value = "/klienci/{projectId}/{year}/{month}", method = RequestMethod.GET, produces = "application/pdf")
    public CompletionStage<ResponseEntity<byte[]>> downloadPDFFile(@PathVariable UUID projectId, @PathVariable int year, @PathVariable int month) {

        var headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "inline; filename=report " + year + "-" + month + ".zip");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        var dateFrom = LocalDate.of(year, month, 1);
        var dateTo = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
        return projection
            .find(projectId, dateFrom, dateTo)
            .toCompletionStage()
            .thenApplyAsync(it -> {
                var result = getAsZip(it);
                return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(result.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(result);
            });

    }

    @SneakyThrows
    byte[] produceReport(Array<ListItem> items) {
        // https://stackoverflow.com/questions/25240541/how-to-add-newpage-in-rmarkdown-in-a-smart-way
        // https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/PdfConverter.java

        var fontsDir = "/home/siudeks/git/sin-net-reports/webapi/main/.fonts";
        var html1 = "<html lang='en'>\n"
            + "<head>\n"
            + "<title>MyPage</title>\n"
            + "<style type='text/css'>\n"
            + "@font-face {\n"
            + "  font-family: 'noto-sans';\n"
            + "  src: url('file://" + fontsDir + "/NotoSans-Regular.ttf');\n"
            + "}\n"
            + "body {\n"
            + "  font-family: 'noto-sans';\n"
            + "}\n"
            + "</style>\n"
            + "</head>\n"
            + "<body>\n"
            + "<h1>Convert HTML to PDF</h1>\n"
            + "<p>Here is an embedded image</p>\n"
            + "<img src='image.png' width='250' height='150'>\n"
            + "<p style='color:red'>Styled text using Inline CSS</p>\n"
            + "<p>Sławomir!</p>\n"
            + "<i>This is italicised text</i>\n"
            + "<p class='fontclass'>This text uses the styling from font face font</p>\n"
            + "<p class='myclass'>This text uses the styling from external CSS class</p>\n"
            + "</body>\n"
            + "</html>\n";
        String nonLatinFontsStyle =
        "@font-face {\n"
        + "  font-family: 'noto-sans';\n"
        + "  src: url('file://" + fontsDir + "/NotoSans-Regular.ttf');\n"
        + "}\n"
        + "\n"
        + "body {\n"
        + "    font-family: 'noto-sans';\n"
        // + "    overflow: hidden;\n"
        + "    word-wrap: break-word;\n"
        + "    font-size: 14px;\n"
        + "}\n"
        + "\n"
        + "var,\n"
        + "code,\n"
        + "kbd,\n"
        + "";


        var is = getClass().getClassLoader().getResourceAsStream("markdown.css");
        var css1 = IOUtils.toString(is);

        @Cleanup
        var os = new ByteArrayOutputStream();
        {
            @Cleanup
            var document = new Document();
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, os);

            // step 3: we open the document
            document.open();
            // step 4: we add a paragraph to the document
            document.add(new Paragraph("Wersja próbna raportu"));
            final int numberOfColumns = 3;
            var table = new PdfPTable(numberOfColumns);
            var cell = new PdfPCell(new Paragraph("header with colspan 3"));
            final var some1 = 3;
            cell.setColspan(some1);
            table.addCell(cell);
            table.addCell("1.1");
            table.addCell("2.1");
            table.addCell("3.1");
            table.addCell("1.2");
            table.addCell("2.2");
            table.addCell("3.2");
            cell = new PdfPCell(new Paragraph("cell test1"));

            final int maxColorValue = 255;
            cell.setBorderColor(new Color(maxColorValue, 0, 0));
            table.addCell(cell);
            cell = new PdfPCell(new Paragraph("cell test2"));
            cell.setColspan(2);
            final var some2 = 0xC0;
            cell.setBackgroundColor(new Color(some2, some2, some2));
            table.addCell(cell);
            document.add(table);
            final int maxWidthPercentage = 100;
            table.setWidthPercentage(maxWidthPercentage);
            document.add(table);
            final int halfWidthPercentage = 50;
            table.setWidthPercentage(halfWidthPercentage);
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);
            document.add(table);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            document.add(table);
        }

        return os.toByteArray();
    }

    @SneakyThrows
    byte[] getAsZip(Array<ListItem> items) {

        @Cleanup
        var baos = new ByteArrayOutputStream();
        {
            var customers = items.groupBy(it -> it.getValue().getWhom());

            @Cleanup
            var zos = new ZipOutputStream(baos);

            for (var c: customers) {
                var customerName = c._2.head().getCustomerName();
                var entry = new ZipEntry(customerName + ".pdf");
                var itemsForCustomer = c._2;
                zos.putNextEntry(entry);
                zos.write(produceReport(itemsForCustomer));
                zos.closeEntry();
            }

        }

        return baos.toByteArray();

    }
}

