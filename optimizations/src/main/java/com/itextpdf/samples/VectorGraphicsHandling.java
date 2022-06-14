package com.itextpdf.samples;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.licensing.base.LicenseKey;
import com.itextpdf.svg.converter.SvgConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class VectorGraphicsHandling {

    private static final String DEST_PDF_CANVAS = "./target/samples/pdfCanvasGraphics.pdf";
    private static final String DEST_SVG_CONVERTER = "./target/samples/svgConverterGraphics.pdf";

    private static final String LOGO = "./src/main/resources/img/itext.svg";

    private static final String LICENSE_ENV = "ITEXT7_PRODUCTS_LICENSE";

    public static void main(String[] args) throws IOException {
        loadLicense();
        createTargetDirectory();

        new VectorGraphicsHandling().createPdfs();
    }

    public void createPdfs() throws IOException {
        createPdfCanvasGraphicsPdf();
        createSvgConverterGraphicsPdf();
    }

    public void createPdfCanvasGraphicsPdf() throws IOException {
        try (PdfWriter writer = new PdfWriter(DEST_PDF_CANVAS);
                PdfDocument pdfDocument = new PdfDocument(writer)) {
            PdfPage page1 = pdfDocument.addNewPage();

            PdfCanvas canvas = new PdfCanvas(page1);
            canvas
                    .saveState()
                    .setLineWidth(30)
                    .moveTo(36, 700)
                    .lineTo(300, 300)
                    .stroke()
                    .restoreState();
            canvas
                    .saveState()
                    .setLineWidth(5)
                    .arc(400, 650, 550, 750, 0, 180)
                    .stroke()
                    .restoreState();

            // .....

            canvas
                    .saveState()
                    .setLineWidth(5)
                    .moveTo(400, 550)
                    .curveTo(500, 570, 450, 450, 550, 550)
                    .stroke()
                    .restoreState();
        }
    }

    public void createSvgConverterGraphicsPdf() throws IOException {
        try (PdfWriter writer = new PdfWriter(DEST_SVG_CONVERTER);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document doc = new Document(pdfDocument);
                InputStream fis = Files.newInputStream(Paths.get(LOGO), StandardOpenOption.READ)
        ) {

            Image image = SvgConverter.convertToImage(fis, pdfDocument);
            doc.add(image);
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
        new File(DEST_SVG_CONVERTER).getParentFile().mkdirs();
    }
}
