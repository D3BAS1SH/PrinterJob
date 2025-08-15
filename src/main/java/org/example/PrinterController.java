package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.IOException;
import java.io.InputStream;

public class PrintController {

    /**
     * Main method to handle printing any supported document.
     *
     * @param inputStream The stream of the file to be printed.
     * @param fileName The name of the file, used to determine the file type.
     * @throws IOException If there's an error reading the file.
     * @throws PrintException If there's an error sending the job to the printer.
     */
    public void printDocument(InputStream inputStream, String fileName) throws IOException, PrintException {
        if (inputStream == null || fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("InputStream and fileName cannot be null or empty.");
        }

        String fileExtension = getFileExtension(fileName);
        BufferedImage rasterImage;

        System.out.println("Processing file: " + fileName + " (Type: " + fileExtension + ")");

        // --- 1. Convert the input file to a standard raster image (BufferedImage) ---
        switch (fileExtension) {
            case "pdf":
                // Use Apache PDFBox to render the first page of the PDF to an image
                try (PDDocument document = PDDocument.load(inputStream)) {
                    PDFRenderer renderer = new PDFRenderer(document);
                    // Render the first page (index 0) at 300 DPI
                    rasterImage = renderer.renderImageWithDPI(0, 300);
                }
                break;

            case "jpg":
            case "jpeg":
            case "png":
            case "bmp":
            case "gif":
                // Use Java's built-in ImageIO to read standard image formats
                rasterImage = ImageIO.read(inputStream);
                if (rasterImage == null) {
                    throw new IOException("Could not decode the input image.");
                }
                break;

            case "webp":
                // Note: Java's ImageIO does not support WEBP by default.
                // You would need to add a third-party ImageIO plugin like 'webp-imageio'.
                // For now, we'll throw an exception.
                throw new UnsupportedOperationException("WEBP format is not supported by default. Please add an ImageIO plugin.");

            default:
                throw new UnsupportedOperationException("Unsupported file type: " + fileExtension);
        }

        System.out.println("Successfully converted document to raster image.");

        // --- 2. Send the raster image to the printer ---
        sendToPrinter(rasterImage);
    }

    /**
     * Sends a BufferedImage to the default printer.
     *
     * @param image The raster image to be printed.
     * @throws PrintException If the print job fails.
     */
    private void sendToPrinter(BufferedImage image) throws PrintException {
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultPrinter == null) {
            throw new PrintException("No default printer found.");
        }
        System.out.println("Sending raster data to printer: " + defaultPrinter.getName());

        DocPrintJob printJob = defaultPrinter.createPrintJob();

        // Create a Printable object from our BufferedImage
        Printable printable = (graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            // Scale the image to fit the printable area of the page
            double scaleX = pageFormat.getImageableWidth() / image.getWidth();
            double scaleY = pageFormat.getImageableHeight() / image.getHeight();
            double scale = Math.min(scaleX, scaleY);

            double x = pageFormat.getImageableX() + (pageFormat.getImageableWidth() - image.getWidth() * scale) / 2;
            double y = pageFormat.getImageableY() + (pageFormat.getImageableHeight() - image.getHeight() * scale) / 2;

            graphics.drawImage(image, (int) x, (int) y, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), null);
            return Printable.PAGE_EXISTS;
        };

        // Create a Doc from the Printable object
        Doc doc = new SimpleDoc(printable, DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);

        printJob.print(doc, new HashPrintRequestAttributeSet());
        System.out.println("Print job successfully sent to the queue.");
    }

    /**
     * Helper method to get the file extension from a file name.
     */
    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOf + 1).toLowerCase();
    }
}
