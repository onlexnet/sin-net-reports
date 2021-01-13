package sinnet;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(path = "/api/raporty")
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
    Optional<byte[]> produceReport(Array<ListItem> items) {
        if (items.isEmpty()) return Optional.empty();

        var sample = items.head();
        var customerName = sample.getCustomerName();
        var customerCity = sample.getCustomerCity();
        var customerAddress = sample.getCustomerAddress();
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

            var headParam = new Paragraph(customerName + " " + Objects.toString(customerCity, "") + " " + Objects.toString(customerAddress, ""));
            var font = new Font(Font.BOLD);
            headParam.setFont(font);
            document.add(headParam);

            final int numberOfColumns = 3;
            final int numberOfColumns5 = 5;
            var table1 = new PdfPTable(numberOfColumns5);
            for (var item: items) {
                table1.addCell(item.getValue().getWho().getValue());
                table1.addCell(item.getValue().getWhen().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                table1.addCell(item.getValue().getWhat());
                table1.addCell(Integer.toString(item.getValue().getHowLong().getValue()));
                table1.addCell(Integer.toString(item.getValue().getHowFar().getValue()));
            }

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

        return Optional.of(os.toByteArray());
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
                var o = produceReport(itemsForCustomer);
                if (!o.isPresent()) continue;
                zos.putNextEntry(entry);
                zos.write(o.get());
                zos.closeEntry();
            }

        }

        return baos.toByteArray();

    }
}

