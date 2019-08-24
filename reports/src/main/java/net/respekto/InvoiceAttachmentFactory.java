package net.respekto;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class InvoiceAttachmentFactory {

    @SneakyThrows
    public static byte[] ToContent(@NonNull InvoiceAttachmentModel model) {
        @Cleanup
        val output = new ByteArrayOutputStream();

        val page = new PDPage();

        try (val doc = new PDDocument()) {
            doc.addPage(page);

            try (val content = new PDPageContentStream(doc, page)) {
                content.beginText();
                content.setFont(PDType1Font.HELVETICA, 26);
                content.newLineAtOffset(250, 750);
                content.showText(model.getCustomerName());
                content.endText();
            }

            try (val content = new PDPageContentStream(doc, page)) {
                String[][] samples = {{"a","b", "1"},
                {"c","d", "2"},
                {"e","f", "3"},
                {"g","h", "4"},
                {"i","j", "5"}} ;
                
                drawTable(page, content, 700, 100, samples);
            }

            doc.save(output);
        }

        return output.toByteArray();
    }

    /**
     * @see http://fahdshariff.blogspot.com/2010/10/creating-tables-with-pdfbox.html
     * @param page
     * @param contentStream
     * @param y             the y-coordinate of the first row
     * @param margin        the padding on left and right of table
     * @param content       a 2d array containing the table data
     */
    @SneakyThrows
    private static void drawTable(PDPage page, PDPageContentStream contentStream, float y, float margin,
            String[][] content) {
        final int rows = content.length;
        final int cols = content[0].length;
        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        final float tableHeight = rowHeight * rows;
        final float colWidth = tableWidth / (float) cols;
        final float cellMargin = 5f;

        // draw the rows
        float nexty = y;
        for (int i = 0; i <= rows; i++) {
            contentStream.drawLine(margin, nexty, margin + tableWidth, nexty);
            nexty -= rowHeight;
        }

        // draw the columns
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            contentStream.drawLine(nextx, y, nextx, y - tableHeight);
            nextx += colWidth;
        }

        // now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        float textx = margin + cellMargin;
        float texty = y - 15;
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                String text = content[i][j];
                contentStream.beginText();
                contentStream.moveTextPositionByAmount(textx, texty);
                contentStream.drawString(text);
                contentStream.endText();
                textx += colWidth;
            }
            texty -= rowHeight;
            textx = margin + cellMargin;
        }
    }
}