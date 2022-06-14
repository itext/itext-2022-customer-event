package com.itextpdf.samples;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.licensing.base.LicenseKey;
import com.itextpdf.pdfoptimizer.PdfOptimizer;
import com.itextpdf.pdfoptimizer.PdfOptimizerFactory;
import com.itextpdf.pdfoptimizer.PdfOptimizerProfile;
import com.itextpdf.pdfoptimizer.handlers.ImageQualityOptimizer;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.BitmapDeindexer;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.BitmapIndexer;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.BitmapScalingProcessor;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.CombinedImageProcessor;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.IImageProcessor;
import com.itextpdf.pdfoptimizer.handlers.imagequality.processors.scaling.AverageCalculationAlgorithm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class PdfOptimizerHandling {
    private static final String FLAG_PDF = "./src/main/resources/pdf/Flag.pdf";
    private static final String DEST_JPEG = "./target/samples/Flag_high-compression (JPEG).pdf";
    private static final String DEST_DOWNSCALE = "./target/samples/Flag_downscaled.pdf";

    private static final String HTML_FORM_I = "./src/main/resources/pdf/htmlform_filled%s.pdf";
    private static final String DEST_FORM_MERGED = "./target/samples/htmlform_merged.pdf";
    private static final String DEST_FORM_OPTIMIZED = "./target/samples/htmlform_merged_optimized.pdf";

    private static final String LICENSE_ENV = "ITEXT7_PRODUCTS_LICENSE";

    public static void main(String[] args) throws IOException {
        loadLicense();
        createTargetDirectory();

        new PdfOptimizerHandling().createPdfs();
    }

    public void createPdfs() throws IOException {
        optimizeFlagJpeg();
        optimizeFlagDownscale();
        optimizeMergedForm();
    }

    private void optimizeFlagJpeg() throws IOException {
        PdfOptimizer optimizer =
                PdfOptimizerFactory.getPdfOptimizerByProfile(PdfOptimizerProfile.HIGH_COMPRESSION);
        optimizer.optimize(new File(FLAG_PDF), new File(DEST_JPEG));
    }

    private void optimizeFlagDownscale() throws IOException {
        double scalingFactor = 0.03;
        IImageProcessor bitmapProcessor = new CombinedImageProcessor()
                .addProcessor(new BitmapDeindexer())
                .addProcessor(new BitmapScalingProcessor(scalingFactor, new AverageCalculationAlgorithm()))
                .addProcessor(new BitmapIndexer());

        new PdfOptimizer()
                .addOptimizationHandler(
                        new ImageQualityOptimizer().setPngProcessor(bitmapProcessor)
                )
                .optimize(new File(FLAG_PDF), new File(DEST_DOWNSCALE));
    }

    private void optimizeMergedForm() throws IOException {
        flattenAndMergeForms();

        PdfOptimizer optimizer =
                PdfOptimizerFactory.getPdfOptimizerByProfile(PdfOptimizerProfile.HIGH_COMPRESSION);
        optimizer.optimize(new File(DEST_FORM_MERGED), new File(DEST_FORM_OPTIMIZED));
    }

    private void flattenAndMergeForms() throws IOException {
        try (PdfWriter writer = new PdfWriter(DEST_FORM_MERGED);
                PdfDocument mergedPdf = new PdfDocument(writer)) {
            PdfMerger merger = new PdfMerger(mergedPdf);
            for (int i = 0; i < 10; i++) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (PdfReader reader = new PdfReader(String.format(HTML_FORM_I, i))) {
                    PdfDocument flattenToMemory = new PdfDocument(
                            reader,
                            new PdfWriter(baos));
                    PdfAcroForm.getAcroForm(flattenToMemory, true).flattenFields();
                    flattenToMemory.close();
                }
                PdfDocument fromInMemory = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
                merger.merge(fromInMemory, 1, 1);
                fromInMemory.close();
            }
            merger.close();
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
        new File(DEST_JPEG).getParentFile().mkdirs();
    }
}
