
package com.testx.web.api.selenium.restassured.qe.util.feedutils;

import com.testx.web.api.selenium.restassured.qe.common.utils.FileUtil;
import com.testx.web.api.selenium.restassured.qe.util.ResourceReaderUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class XMLFileOperationUtil {

    //Tested
    public static List<String> getSpecificColumnForDownstreamXML(String fileName, String columnName) {
        Document doc = ResourceReaderUtil.readXmlFile(fileName);
        try {
            NodeList emmlList = doc.getElementsByTagName("book");
            List<String> recordData = new ArrayList<>();
            for (int i = 0; i < emmlList.getLength(); i++) {
                Node emmlNode = emmlList.item(i);
                if (emmlNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element emmlElement = (Element) emmlNode;
                    Node item = emmlElement.getElementsByTagName(columnName).item(0);
                    String itemValue = (item != null) ? item.getTextContent() : "";
                    recordData.add(itemValue);
                }
            }
            return recordData;
        } catch (Exception e) {
            // Exception handled silently - consider adding logging if needed
        }
        return null;
    }

    public static Map<List<String>, List<String>> getAllRecordForDownStreamAsMap(String fileName) {
        Document doc = ResourceReaderUtil.readXmlFile(fileName);
        try {
            Map<List<String>, List<String>> dataMap = new HashMap<>();
            NodeList emmlList = doc.getElementsByTagName("emm1");
            for (int i = 0; i < emmlList.getLength(); i++) {
                List<String> recordData = new ArrayList<>();
                Node emmlNode = emmlList.item(i);
                if (emmlNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element emmlElement = (Element) emmlNode;
                    Node dealIdElement = emmlElement.getElementsByTagName("dealId").item(0);
                    String dealId = (dealIdElement != null) ? dealIdElement.getTextContent() : "";
                    recordData.add(dealId);
                    Node dealNameElement = emmlElement.getElementsByTagName("dealName").item(0);
                    String dealName = (dealNameElement != null) ? dealNameElement.getTextContent() : "";
                    recordData.add(dealName);
                    Node personIdElement = emmlElement.getElementsByTagName("personId").item(0);
                    String personId = (personIdElement != null) ? personIdElement.getTextContent() : "";
                    recordData.add(personId);
                    Node firstNameElement = emmlElement.getElementsByTagName("firstName").item(0);
                    String firstName = (firstNameElement != null) ? firstNameElement.getTextContent() : "";
                    recordData.add(firstName);
                    Node lastNameElement = emmlElement.getElementsByTagName("lastName").item(0);
                    String lastName = (lastNameElement != null) ? lastNameElement.getTextContent() : "";
                    recordData.add(lastName);
                    Node overallNameElement = emmlElement.getElementsByTagName("overallName").item(0);
                    String overallName = (overallNameElement != null) ? overallNameElement.getTextContent() : "";
                    recordData.add(overallName);
                    Node teamJoinDateElement = emmlElement.getElementsByTagName("teamJoinDate").item(0);
                    String teamJoinDate = (teamJoinDateElement != null) ? teamJoinDateElement.getTextContent() : "";
                    recordData.add(teamJoinDate);
                    Node teamRoleElement = emmlElement.getElementsByTagName("teamRole").item(0);
                    String teamRole = (teamRoleElement != null) ? teamRoleElement.getTextContent() : "";
                    recordData.add(teamRole);
                    if (teamRole.equals("Over the Wall")) {
                        List<String> approverRoleList = new ArrayList<>();
                        NodeList overTheWallApproverDetailsList = emmlElement.getElementsByTagName("overTheWallApproverDetails");
                        int arraySize = overTheWallApproverDetailsList.getLength();
                        for (int otwadl = 0; otwadl < arraySize; otwadl++) {
                            Element overTheWallApproverDetails = (Element) overTheWallApproverDetailsList.item(otwadl);
                            approverRoleList.add(String.valueOf(overTheWallApproverDetails));
                        }

                        dataMap.put(recordData, recordData);
                    }
                }
            }
            return dataMap;
        } catch (Exception e) {
        }
        return null;
    }

    //Tested
    public static Map<List<String>, List<String>> readXMLAsMap() {
        String xmlData;
        try {
            xmlData = FileUtil.readFileAsString("src/test/resources/feedfiles/reviews.xml");
            System.out.println(xmlData);

            JAXBContext jaxbContext = JAXBContext.newInstance(BooksWrapper.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            // Parse the <books> element and its contents
            BooksWrapper booksWrapper = (BooksWrapper) unmarshaller.unmarshal(new StringReader(xmlData));

            // Get the list of books from the wrapper
            List<Book> books = booksWrapper.getBooks();

            Map<List<String>, List<String>> map = new HashMap<>();

            // Now you can iterate through the list of books
            for (Book book : books) {
                List<String> list = new ArrayList<>();
                list.add(String.valueOf(book.getId()));
                list.add(String.valueOf(book.getEmail()));
                list.add(String.valueOf(book.getTimestamp()));
                list.add(String.valueOf(book.getRating()));
                list.add(String.valueOf(book.getReviewId()));
                list.add(String.valueOf(book.getReview()));

                // Using a unique key for each entry in the map, such as the book's ID
                map.put(list, list);
            }
            return map;
        } catch (JAXBException | IOException e) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static void changeXml(String fileName, String filePath) throws IOException {
        BufferedReader bufferReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        List<String> list = new ArrayList<>();
        String line;

        int count = 0;
        while ((line = bufferReader.readLine()) != null) {
            if (line.trim().equals("<?xml version=\"1.8\" encoding=\"UTF-8\" standalone=\"yes\"?>")) {
                count++;
            } else list.add(line);
        }
        bufferReader.close();
        if (count > 1) {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
            bufferedWriter.newLine();
            bufferedWriter.write("<root>");
            bufferedWriter.newLine();
            for (String l : list) {
                bufferedWriter.write(l);
                bufferedWriter.newLine();
            }
            bufferedWriter.write("</root>");
            bufferedWriter.close();
        }
    }

    public static Document readXmlFile(String fileName) {
        File[] files = ResourceReaderUtil.getFile(fileName);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        Document document;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        assert files != null;
        try {
            document = documentBuilder.parse(files[0]);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        document.getDocumentElement().normalize();
        return document;
    }

    public static Map<List<String>, List<String>> getCompositionKeyForliLDataAsMap(String fileName) {
        Document doc = readXmlFile(fileName);
        Map<List<String>, List<String>> dataMap = new HashMap<>();
        NodeList emmlList = doc.getElementsByTagName("masterTagName");
        for (int i = 0; i < emmlList.getLength(); i++) {
            List<String> recordData = new ArrayList<>();
            Node emmlNode = emmlList.item(i);
            if (emmlNode.getNodeType() == Node.ELEMENT_NODE) {
                Element emmlElement = (Element) emmlNode;
                Node dealIdElement = emmlElement.getElementsByTagName("dealId").item(0);
                String dealId = (dealIdElement != null) ? dealIdElement.getTextContent() : "";
                recordData.add(dealId);

                Node personIdElement = emmlElement.getElementsByTagName("partyID").item(0);
                String personId = (personIdElement != null) ? personIdElement.getTextContent() : "";
                recordData.add(personId);

                dataMap.put(recordData, recordData);
                return null;
            }
        }
        return dataMap;
    }

    public static Set<String> getDuplicateRecords(String fileName) {
        Map<List<String>, List<String>> allRecordForDownStreamAsMap = getCompositionKeyForliLDataAsMap(fileName);

        Set<String> uniqueRecord = new HashSet<>();
        Set<String> duplicateRecord = new HashSet<>();

        assert allRecordForDownStreamAsMap != null;

        for (Map.Entry<List<String>, List<String>> current : allRecordForDownStreamAsMap.entrySet()) {
            if (!uniqueRecord.add(String.valueOf(current.getValue()))) {
                duplicateRecord.add(String.valueOf(current.getValue()));
            }
        }
        return duplicateRecord;
    }

    public static String getElementValue(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return (element != null) ? element.getTextContent() : "";
        }
        return "";
    }

    public static int getRecordCount(String fileName, String tagName) {
        Document document = readXmlFile(fileName);
        NodeList nodeList = document.getElementsByTagName(tagName);
        return nodeList.getLength();
    }
}