package sinnet;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.collection.Array;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import sinnet.models.ActionDuration;
import sinnet.models.Distance;
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

            var baseFont = new Font();
            final var fontSize = 10;
            baseFont.setSize(fontSize);

            var header = Objects.toString(customerName, "Brak przypisanego kontrahenta")
            + " " + Objects.toString(customerCity, "")
            + " " + Objects.toString(customerAddress, "");
            var headParam = new Paragraph(header);
            headParam.setFont(baseFont);
            document.add(headParam);
            document.add(new Paragraph("-"));

            final int col1width = 3;
            final int col2width = 3;
            final int col3width = 12;
            final int col4width = 2;
            final int col5width = 2;
            var table = new PdfPTable(col1width + col2width + col3width + col4width + col5width);
            final int maxWidthPercentage = 100;
            table.setWidthPercentage(maxWidthPercentage);

            @AllArgsConstructor
            class CellParams {
                private String text;
                private Integer width;
                private Boolean alighToRight;
            }
            var addValue = (Consumer<CellParams>) v -> {
                var p = new Paragraph(v.text);
                p.setFont(baseFont);
                var aCell = new PdfPCell(p);
                if (v.alighToRight) aCell.setHorizontalAlignment(HorizontalAlignment.RIGHT.getId());
                aCell.setColspan(v.width);
                table.addCell(aCell);
            };
            var sumTime = 0;
            var sumDistance = 0;
            addValue.accept(new CellParams("Serwisant", col1width, false));
            addValue.accept(new CellParams("Dzień", col2width, false));
            addValue.accept(new CellParams("Praca wykonana", col3width, false));
            addValue.accept(new CellParams("Czas", col4width, true));
            addValue.accept(new CellParams("KM", col5width, true));
            for (var item: items) {
                var howLong = item.getValue().getHowLong();
                var distance = item.getValue().getHowFar();
                var who = Optional
                    .ofNullable(item.getValue().getWho().getValue())
                    .map(it -> it.split("@")[0])
                    .orElse(null);
                addValue.accept(new CellParams(who, col1width, false));
                addValue.accept(new CellParams(item.getValue().getWhen().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), col2width, false));
                addValue.accept(new CellParams(item.getValue().getWhat(), col3width, false));
                addValue.accept(new CellParams(howLong.toString(), col4width, true));
                addValue.accept(new CellParams(distance.toString(), col5width, true));
                sumTime += howLong.getValue();
                sumDistance += distance.getValue();
            }
            addValue.accept(new CellParams(null, col1width, false));
            addValue.accept(new CellParams(null, col2width, false));
            addValue.accept(new CellParams(null, col3width, false));
            addValue.accept(new CellParams(ActionDuration.of(sumTime).toString(), col4width, true));
            addValue.accept(new CellParams(Distance.of(sumDistance).toString(), col5width, true));
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

