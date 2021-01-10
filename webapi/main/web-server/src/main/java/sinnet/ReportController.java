package sinnet;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.pdf.converter.PdfConverterExtension;
import com.vladsch.flexmark.profile.pegdown.Extensions;
import com.vladsch.flexmark.profile.pegdown.PegdownOptionsAdapter;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.Cleanup;
import lombok.SneakyThrows;

@RestController
@RequestMapping(path = "/raporty")
public class ReportController {

    @RequestMapping(value = "/klienci", method = RequestMethod.GET, produces = "application/pdf")
    public ResponseEntity<byte[]> downloadPDFFile() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "inline; filename=report.zip");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        var result = getAsZip();
        return ResponseEntity.ok().headers(headers).contentLength(result.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(result);
    }

    static final DataHolder OPTIONS = PegdownOptionsAdapter.flexmarkOptions(
            Extensions.ALL & ~(Extensions.ANCHORLINKS | Extensions.EXTANCHORLINKS_WRAP), TocExtension.create()).toMutable()
            .set(TocExtension.LIST_CLASS, PdfConverterExtension.DEFAULT_TOC_LIST_CLASS)
            .toImmutable();

    @SneakyThrows
    static byte[] produceReport() {
        // https://stackoverflow.com/questions/25240541/how-to-add-newpage-in-rmarkdown-in-a-smart-way
        // https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/PdfConverter.java
        var content = "hello world "
        + "\\newpage "
        + "```{r, echo=FALSE} "
        + "1+1 "
        + "``` "
        + " <P style='page-break-before: always'> "

        + "\\pagebreak "
        + "```{r, echo=FALSE} "
        + "plot(1:10) "
        + "```";

        var options = new MutableDataSet();

        // uncomment to set optional extensions
        // options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        // options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        var parser = Parser.builder(options).build();
        var node = parser.parse(content);
        var renderer = HtmlRenderer.builder(options).build();
        var html = renderer.render(node);

        var bs = new ByteArrayOutputStream();
        var os = new ObjectOutputStream(bs);
        PdfConverterExtension.exportToPdf(os, html, "", OPTIONS);
        return bs.toByteArray();
    }

    @SneakyThrows
    static byte[] getAsZip() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        {
        @Cleanup
            var zos = new ZipOutputStream(baos);

            /* File is not on the disk, test.txt indicates
                only the file name to be put into the zip */
            var entry1 = new ZipEntry("test1.pdf");
            var entry2 = new ZipEntry("test2.pdf");

            zos.putNextEntry(entry1);
            zos.write(produceReport());
            zos.closeEntry();

            zos.putNextEntry(entry2);
            zos.write(produceReport());
            zos.closeEntry();
        }

        return baos.toByteArray();

    }
}

