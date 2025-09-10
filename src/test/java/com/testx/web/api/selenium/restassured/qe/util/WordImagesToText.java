
package com.testx.web.api.selenium.restassured.qe.util;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.tika.exception.TikaException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class WordImagesToText {

    public static void convertImagetoText() throws IOException {
        FileInputStream fis = new FileInputStream("src/main/resources/img.png");
        Tesseract instance = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        try {

            BufferedImage bufferedImage = ImageIO.read(fis);
            String result = instance.doOCR(bufferedImage);
            System.out.println(result);

        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void convertImagestoText() throws IOException {
        FileInputStream fis = new FileInputStream("src/main/resources/data_reconcile.docx");
        XWPFDocument document = new XWPFDocument(fis);
        List<XWPFPictureData> pictures = document.getAllPictures();

        FileWriter fileWriter=new FileWriter("src/main/resources/output.java");
        for (XWPFPictureData picture : pictures) {
            byte[] pictureData = picture.getData();
            Tesseract instance = new Tesseract();
            File tessDataFolder = LoadLibs.extractTessResources("tessdata");
            instance.setDatapath(tessDataFolder.getAbsolutePath());
            try {
                InputStream imageStream = new ByteArrayInputStream(pictureData);
                BufferedImage bufferedImage = ImageIO.read(imageStream);
                String result = instance.doOCR(bufferedImage);
                System.out.println(result);
                fileWriter.write(result);
            } catch (TesseractException e) {
                System.err.println(e.getMessage());
            }
        }
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException, TesseractException, TikaException {

        convertImagetoText();
    }

}
