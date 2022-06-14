package com.itextpdf.samples;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.licensing.base.LicenseKey;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HtmlPagedMedia {
    private static final String HTML = "./src/main/resources/html/pagedMedia.html";
    private static final String DEST = "./target/samples/htmlPagedMedia.pdf";
    private static final String LICENSE_ENV = "ITEXT7_PRODUCTS_LICENSE";

    public static void main(String[] args) throws IOException {
        loadLicense();
        createTargetDirectory();

        new HtmlPagedMedia().createPdf();
    }

    public void createPdf() throws IOException {
        HtmlConverter.convertToPdf(
                new File(HTML), new File(DEST),
                new ConverterProperties()
                        .setMediaDeviceDescription(new MediaDeviceDescription(MediaType.PRINT))
        );
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
