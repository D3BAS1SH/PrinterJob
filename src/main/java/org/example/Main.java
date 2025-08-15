package org.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        // --- This is where you choose which file to print ---
        String fileNameToPrint = "Tejaswini.pdf"; // Change this to "sample.png" to test an image

        try {
            File file = new File(fileNameToPrint);
            if (!file.exists()) {
                System.err.println("Error: File not found at " + file.getAbsolutePath());
                System.err.println("Please make sure '" + fileNameToPrint + "' is in the project's root directory.");
                return;
            }

            // Get an InputStream for the file
            InputStream fileStream = new FileInputStream(file);

            // Create an instance of our controller and call the print method
            PrintController printController = new PrintController();
            printController.printDocument(fileStream, fileNameToPrint);

        } catch (Exception e) {
            System.err.println("An error occurred during the print process:");
            e.printStackTrace();
        }
    }
}