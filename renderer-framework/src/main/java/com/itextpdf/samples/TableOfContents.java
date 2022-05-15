package com.itextpdf.samples;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TextRenderer;
import com.itextpdf.licensing.base.LicenseKey;

import com.thedeanda.lorem.LoremIpsum;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A sample which demonstrates how iText 7 Render Framework can be utilized to create the document's
 * table of contents.
 */
public class TableOfContents {

    /**
     * The path to the resultant PDF file.
     */
    private static final String DEST = "./target/samples/tableOfContents.pdf";

    /**
     * The name of the environment variable which stores the path to the license.
     */
    private static final String LICENSE_ENV = "ITEXT7_PRODUCTS_LICENSE";

    /**
     * The main method of this example.
     *
     * @param args no arguments are needed to run this example.
     * @throws IOException signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
        loadLicense();
        createTargetDirectory();

        new TableOfContents().createPdf();
    }

    /**
     * Creates the PDF file.
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public void createPdf() throws IOException {
        Document document = new Document(new PdfDocument(new PdfWriter(DEST)));

        List<String> tocTitles = new ArrayList<>();
        List<Integer> tocItemPages = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String titleText = "Paragraph №" + (i + 1);
            Text title = new Text(titleText).setFontSize(20);
            title.setNextRenderer(new TOCTextRenderer(title, tocItemPages));

            tocTitles.add(titleText);

            document.add(new Paragraph()
                    .add(title)
                    .add("\n")
                    .add(LoremIpsum.getInstance().getWords(100)));
        }

        document.add(new AreaBreak());

        // Create the table of contents
        document.add(new Paragraph("Table of Contents").setFontSize(20));
        for (int i = 0; i < tocItemPages.size(); i++) {
            document.add(new Paragraph()
                    .add(new Text(tocTitles.get(i)))
                    .addTabStops(new TabStop(523, TabAlignment.RIGHT, new DottedLine()))
                    .add(new Tab())
                    .add(new Text(String.valueOf(tocItemPages.get(i)))));
        }

        document.close();
    }

    /**
     * A custom {@link TextRenderer} with the help of which one can collect all the information
     * related to future table of contents creation.
     */
    private static class TOCTextRenderer extends TextRenderer {
        private final List<Integer> tocItemPages;

        public TOCTextRenderer(Text modelElement, List<Integer> tocItemPages) {
            super(modelElement);
            this.tocItemPages = tocItemPages;
        }

        @Override
        public void draw(DrawContext drawContext) {
            super.draw(drawContext);

            tocItemPages.add(getOccupiedArea().getPageNumber());
        }

        // If a renderer overflows on the next area, iText uses #getNextRenderer() method to create a new renderer
        // for the overflow part. If #getNextRenderer() isn't overridden, the default method will be used and thus
        // the default rather than the custom renderer will be created
        @Override
        public IRenderer getNextRenderer() {
            return new TOCTextRenderer((Text) modelElement, tocItemPages);
        }
    }

    /**
     * Loads the license.
     */
    private static void loadLicense() throws IOException {
        try (FileInputStream license = new FileInputStream(System.getenv(LICENSE_ENV))) {
            LicenseKey.loadLicenseFile(license);
        }
    }

    /**
     * Creates a target directory for the resultant PDF.
     */
    private static void createTargetDirectory() {
        new File(DEST).getParentFile().mkdirs();
    }
}
