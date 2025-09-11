
package com.testx.web.api.selenium.restassured.qe.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;

import com.testx.web.api.selenium.restassured.qe.util.propertyreader.PropertyReader;
import lombok.extern.log4j.Log4j;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

@Log4j
public class ResourceReaderUtil {
    private static String DATA_SOURCE = PropertyReader.datasource_file;
    private static final String filePath = "src/test/resources/feedfiles";

    //Tested
    public static CSVReader readCSVFile(String fileName) {
        CSVReader reader = null;
        File[] files = getFile(fileName);
        try {
            reader = new CSVReader(new FileReader(files[0]));
        } catch (FileNotFoundException e) {
        }
        return reader;
    }

    public static JsonArray getResource() {
        try {
            InputStream resourceAsStream = ResourceReaderUtil.class.getResourceAsStream("/" + DATA_SOURCE);
            JsonParser jsonParser = new JsonParser();
            assert resourceAsStream != null;
            return jsonParser.parse(new InputStreamReader(resourceAsStream)).getAsJsonArray();
        } catch (Exception exception) {
            log.info("Some Issue While Reading the Resource " + exception.getMessage());
        }
        return null;
    }

    //Tested
    public static Document readXmlFile(String fileName) {
        File[] files = getFile(fileName);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document document;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            document = documentBuilder.parse(files[0]);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        return document;
    }

    //Tested
    public static File[] getFile(String fileName) {
        File file = new File(filePath);
        return file.listFiles(((dir, name) -> name.startsWith(fileName)));
    }

    //Tested
    public static BufferedReader getBufferReaderStream(String fileName) {
        File[] files = getFile(fileName);
        BufferedReader bufferedReader = null;
        try {
            assert files != null;
            bufferedReader = new BufferedReader(new FileReader(files[0]));
        } catch (FileNotFoundException e) {
        }
        return bufferedReader;
    }
}
