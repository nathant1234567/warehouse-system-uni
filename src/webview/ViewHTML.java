package webview;

import webview.converter.Config;

import java.awt.Desktop;
import java.io.File;

public class ViewHTML {
    public static void main(String[] args) {
        try {
            String fileName = Config.SITE_ADDRESS + Config.WAREHOUSE;
             // Open the HTML file in the system's default web browser
            File htmlFile = new File(fileName);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            } else {
                System.out.println("Desktop is not supported. HTML file is at: " + fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
