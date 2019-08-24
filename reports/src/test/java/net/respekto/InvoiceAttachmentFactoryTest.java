package net.respekto;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import lombok.SneakyThrows;
import lombok.val;

public class InvoiceAttachmentFactoryTest {

    //@Test
    @SneakyThrows
    public void shouldProduceOutput() {
        val model = new InvoiceAttachmentModel("customer name");

        val rawData = InvoiceAttachmentFactory.ToContent(model);

        // decompose to model again and check if required parts are available in produced content
        val actual = PDDocument.load(rawData);

        val reader = new PDFTextStripper();
        reader.setStartPage(1);
        reader.setEndPage(1);
        val text = reader.getText(actual);

        Assertions.assertThat(text).isEqualTo("customer name\r\n");

    }
}