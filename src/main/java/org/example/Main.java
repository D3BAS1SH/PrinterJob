package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.Loader;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, java.awt.print.PrinterException {
        // --- This is where you choose which file to print ---
        File pdfFile = new File("E:\\Software\\JavaTests\\PrinterJob\\Tejaswini.pdf");

        // Find the default printer
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultPrinter == null) {
            System.err.println("No default printer found.");
            return;
        }

        // --- THE CHANGE IS HERE ---
        // Load the document using the new Loader class
        PDDocument document = Loader.loadPDF(pdfFile);

        // Create a PrinterJob
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(defaultPrinter);
        job.setPageable(new PDFPageable(document));

        // Send the print job
        System.out.println("Sending print job for: " + pdfFile.getName());
        job.print();

        // Close the document
        document.close();
        System.out.println("Print job sent successfully.");
    }
}