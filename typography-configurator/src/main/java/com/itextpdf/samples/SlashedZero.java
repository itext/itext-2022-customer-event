package com.itextpdf.samples;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.licensing.base.LicenseKey;
import com.itextpdf.typography.config.LatinScriptConfig;
import com.itextpdf.typography.config.TypographyConfigurator;

import com.thedeanda.lorem.LoremIpsum;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A sample which demonstrates how {@link TypographyConfigurator} can be utilized to render zeros
 * as slashed rather than plain.
 */
public class SlashedZero {

    /**
     * The path to the font with the slashed zero feature.
     */
    private static final String FONT = "./src/main/resources/fonts/NotoSans-Regular.ttf";

    /**
     * The path to the resultant PDF file.
     */
    private static final String DEST = "./target/samples/slashedZero.pdf";

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

        new SlashedZero().createPdf();
    }

    /**
     * Creates the PDF file.
     *
     * @throws IOException signals that an I/O exception has occurred
     */
    public void createPdf() throws IOException {
        try (Document document = new Document(new PdfDocument(new PdfWriter(DEST)))) {
            Paragraph headingParagraph = new Paragraph("iText 2022 Customer Event").setFontSize(20);

            PdfFont fontWithSlashedZero = PdfFontFactory.createFont(FONT);
            headingParagraph.setFont(fontWithSlashedZero);

            TypographyConfigurator typographyConfigurator = new TypographyConfigurator()
                    .addFeatureConfig(
                            new LatinScriptConfig()
                                    .appendCustomFeature("zero"));
            headingParagraph.setProperty(Property.TYPOGRAPHY_CONFIG, typographyConfigurator);

            document.add(headingParagraph);
            document.add(new Paragraph(LoremIpsum.getInstance().getWords(50)));
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
