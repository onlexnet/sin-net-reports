package sinnet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.vavr.collection.Array;
import io.vavr.control.Option;
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

    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP), TocExtension.create()).toMutable()
            .set(TocExtension.LIST_CLASS, PdfConverterExtension.DEFAULT_TOC_LIST_CLASS)
            .toImmutable();

    @SneakyThrows
    byte[] produceReport(Array<ListItem> items) {
        // https://stackoverflow.com/questions/25240541/how-to-add-newpage-in-rmarkdown-in-a-smart-way
        // https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/PdfConverter.java

        String fontsDir = "/home/siudeks/git/sin-net-reports/webapi/main/.fonts";
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


        var builder = new StringBuilder();
        builder.append("Wydruk  ");
        builder.append("| Data | Usługa | Czas | Dojazd |\n");
        builder.append("| :----- | :----: | -----: | -----: |\n");
        var source = items.sortBy((v1, v2) -> v1.compareTo(v2), it -> it.getValue().getWhen());
        for (var item: source) {
            var time = item.getValue().getHowLong().getValue();
            final var minutesPerHour = 60;
            var hours = time / minutesPerHour;
            var minutes = Integer.toString(time % minutesPerHour);
            if (minutes.length() == 1) minutes = "0" + minutes;
            builder.append("|");
            builder.append(item.getValue().getWhen());
            builder.append("|");
            builder.append(Option.of(item.getValue().getWhat()).getOrElse("-"));
            builder.append("|");
            builder.append(hours + ":" + minutes);
            builder.append("|");
            builder.append(item.getValue().getHowFar().getValue());
            builder.append("|\n");
        }
        var content = builder.toString();
        var options = new MutableDataSet();

        // uncomment to set optional extensions
        options
            .set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()))
            .set(TablesExtension.WITH_CAPTION, false)
            .set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.MIN_HEADER_ROWS, 1)
            .set(TablesExtension.MAX_HEADER_ROWS, 1)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true);
        // uncomment to convert soft-breaks to hard breaks
        // options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        var parser = Parser.builder(options).build();
        var node = parser.parse(content);
        var renderer = HtmlRenderer.builder(options).build();
        var html = renderer.render(node);

        var is = getClass().getClassLoader().getResourceAsStream("markdown.css");
        var css1 = IOUtils.toString(is);

        var htmlDocdoc = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<style>" + css1 + "</style>\n"
            + "<style>" + nonLatinFontsStyle + "</style>\n"
            + "</head>\n"
            + "<body>\n"
            + "Sławomir"
            + "</body>\n"
            + "</html>";
        var bs = new ByteArrayOutputStream();
        var os = new ObjectOutputStream(bs);


        // https://github.com/vsch/flexmark-java/wiki/PDF-Renderer-Converter
//         // There are more options on the builder than shown below.
//         @Cleanup
//         var builder2 = new PdfRendererBuilder();

//         builder2.toStream(os);
//         var renderer2 = builder.buildPdfRenderer();
//         var document = renderer2.getPdfDocument();
//         builder2.use

//  if (protectionPolicy != null)
//      document.protect(protectionPolicy);

//  renderer.layout();
//  renderer.createPDF();


        OPTIONS.toMutable().set(PdfConverterExtension.PROTECTION_POLICY,
            new StandardProtectionPolicy("opassword", "upassword", new AccessPermission()));
        PdfConverterExtension.exportToPdf(os, htmlDocdoc, "", OPTIONS);

        return bs.toByteArray();
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

