

package com.testx.web.api.selenium.restassured.qe.backend.stepdefs;


import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.Hooks;
import com.testx.web.api.selenium.restassured.qe.util.E2ECompareUtil;
import com.testx.web.api.selenium.restassured.qe.util.dbutils.DBCommonUtils;
import com.testx.web.api.selenium.restassured.qe.util.feedutils.XMLFileOperationUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class XMLAndDBAssertions {

    //Tested
    @When("I verify all record of downstream xml feed file to database")
    public void xmlDownStreamDataCompare(DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");

            if (!tableName.matches("^[a-zA-Z0-9_]+$")) throw new IllegalArgumentException("Invalid table name");
            Map<List<String>, List<String>> allRecordsListDB = DBCommonUtils.getAllRecordsByQueryAsMap("select * from " + tableName);
            System.out.println(allRecordsListDB);
            Map<List<String>, List<String>> allRecordsFeed = XMLFileOperationUtil.readXMLAsMap();
            System.out.println(allRecordsFeed);
            assert allRecordsListDB != null;
            assert allRecordsFeed != null;
            Map<List<String>, List<String>> result = E2ECompareUtil.compareTwoMap(allRecordsListDB, allRecordsFeed);
            Assert.assertEquals(result.size(), 0);
        }
    }

    //Tested
    @When("Verify specific column data for all records for xml downstream")
    public void specificColumnForXmlDownstream(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String fileColumnName = column.get("fileColumnName");
            String dbColumnName = column.get("dbColumnName");
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, dbColumnName);
            List<String> fileData = XMLFileOperationUtil.getSpecificColumnForDownstreamXML(fileName, fileColumnName);

            assert fileData != null;
            Assert.assertEquals(fileData, dbData);
        }

    }

    //Tested
    @When("Verify max value of a specific column with xml feed for downstream")
    public void maxValueForXmlDownstream(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String fileColumnName = column.get("fileColumnName");
            String dbColumnName = column.get("dbColumnName");
            List<String> fileData = XMLFileOperationUtil.getSpecificColumnForDownstreamXML(fileName, fileColumnName);
            assert fileData != null;
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInFile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, dbColumnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).max().orElse(0);
            Hooks.scenario.log("The max value is " + maxValueInFile + " from " + fileColumnName + " column in file");
            Hooks.scenario.log("The max value is " + maxValueInDB + " from " + dbColumnName + " column in DB");
            Assert.assertEquals(maxValueInFile, maxValueInDB);
        }
    }

    //Tested
    @When("Verify avg value of a specific column with xml feed for downstream")
    public void avgValueForXmlDownstream(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String fileColumnName = column.get("fileColumnName");
            String dbColumnName = column.get("dbColumnName");
            List<String> fileData = XMLFileOperationUtil.getSpecificColumnForDownstreamXML(fileName, fileColumnName);
            assert fileData != null;
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double avgInfile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).average().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, dbColumnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double avgValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).average().orElse(0);
            Hooks.scenario.log("The max value is " + avgInfile + " from " + fileColumnName + " column in file");
            Hooks.scenario.log("The max value is " + avgValueInDB + " from " + dbColumnName + " column in DB");
            Assert.assertEquals(String.valueOf(avgInfile), String.valueOf(avgValueInDB));
        }
    }

    //Tested
    @When("Verify sum value of a specific column with xml feed for downstream")
    public void sumValueForXmlDownstream(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String fileColumnName = column.get("fileColumnName");
            String dbColumnName = column.get("dbColumnName");
            List<String> fileData = XMLFileOperationUtil.getSpecificColumnForDownstreamXML(fileName, fileColumnName);
            assert fileData != null;
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double sumInfile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).sum();
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, dbColumnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double sumValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).sum();
            Hooks.scenario.log("The max value is " + sumInfile + " from " + fileColumnName + " column in file");
            Hooks.scenario.log("The max value is " + sumValueInDB + " from " + dbColumnName + " column in DB");
            Assert.assertEquals(String.valueOf(sumInfile), String.valueOf(sumValueInDB));
        }
    }

    //Tested
    @When("Verify min value of a specific column with xml feed for downstream")
    public void minValueForXmlDownstream(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String fileColumnName = column.get("fileColumnName");
            String dbColumnName = column.get("dbColumnName");
            List<String> fileData = XMLFileOperationUtil.getSpecificColumnForDownstreamXML(fileName, fileColumnName);
            assert fileData != null;
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int minValueInFile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).min().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, dbColumnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int minValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).min().orElse(0);
            Hooks.scenario.log("The max value is " + minValueInFile + " from " + fileColumnName + " column in file");
            Hooks.scenario.log("The max value is " + minValueInDB + " from " + dbColumnName + " column in DB");
            Assert.assertEquals(String.valueOf(minValueInDB), String.valueOf(minValueInFile));
        }
    }
}