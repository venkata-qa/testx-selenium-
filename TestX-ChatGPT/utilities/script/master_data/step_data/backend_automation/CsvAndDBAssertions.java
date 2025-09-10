
package com.testx.web.api.selenium.restassured.qe.backend.stepdefs;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.Hooks;
import com.testx.web.api.selenium.restassured.qe.util.E2ECompareUtil;
import com.testx.web.api.selenium.restassured.qe.util.dbutils.DBCommonUtils;
import com.testx.web.api.selenium.restassured.qe.util.feedutils.CSVFileOperationUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j;
import org.testng.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
@Log4j
public class CsvAndDBAssertions {
    //Tested
    @When("Verify avg value of a specific column with csv feed")
    public void avgValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnName = column.get("columnName");
            String columnIndex = column.get("columnIndex");
            List<String> fileData = CSVFileOperationUtil.getSpecificColumnAllValues(fileName, columnIndex);
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            OptionalDouble average = colDataForMaxInFile.stream().mapToInt(Integer::intValue).average();
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            OptionalDouble average1 = colDataForMaxInDB.stream().mapToInt(Integer::intValue).average();
            Hooks.scenario.log("The max value is " + average + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + average1 + " from " + columnName + " column in DB");
            Assert.assertEquals(average, average1);
        }
    }


    //Tested
    @When("Verify sum value of a specific column with csv feed")
    public void sumValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnName = column.get("columnName");
            String columnIndex = column.get("columnIndex");
            List<String> fileData = CSVFileOperationUtil.getSpecificColumnAllValues(fileName, columnIndex);
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInFile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).sum();
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).sum();
            Hooks.scenario.log("The max value is " + maxValueInFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + maxValueInDB + " from " + columnName + " column in DB");
            Assert.assertEquals(maxValueInFile, maxValueInDB);
        }
    }


    //Tested
    @When("Verify min value of a specific column with csv feed")
    public void minValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnName = column.get("columnName");
            String columnIndex = column.get("columnIndex");
            List<String> fileData = CSVFileOperationUtil.getSpecificColumnAllValues(fileName, columnIndex);
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInFile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).min().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).min().orElse(0);
            Hooks.scenario.log("The max value is " + maxValueInFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + maxValueInDB + " from " + columnName + " column in DB");
            Assert.assertEquals(maxValueInFile, maxValueInDB);
        }
    }

    //Tested
    @When("Verify max value of a specific column with csv feed")
    public void maxValueOfSpecificColumnInCsv(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnName = column.get("columnName");
            String columnIndex = column.get("columnIndex");
            List<String> fileData = CSVFileOperationUtil.getSpecificColumnAllValues(fileName, columnIndex);
            List<Integer> colDataForMaxInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInFile = colDataForMaxInFile.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForMaxInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxValueInDB = colDataForMaxInDB.stream().mapToInt(Integer::intValue).max().orElse(0);
            Hooks.scenario.log("The max value is " + maxValueInFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + maxValueInDB + " from " + columnName + " column in DB");
            Assert.assertEquals(maxValueInFile, maxValueInDB);
        }
    }

    //Tested
    @When("I find missing and extra records in db or csv feed files")
    public void findMissingRecordsOfFeedAndDB(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            Map<List<String>, List<String>> allRecordsFromDB = DBCommonUtils.getAllRecordsByQueryAsMap("Select * from " + tableName);
            Map<List<String>, List<String>> allRecordsFromFeed = CSVFileOperationUtil.getAllRecordAsMap(fileName);
            assert allRecordsFromDB != null;
            Hooks.scenario.log("Total number of records in DB are : " + allRecordsFromDB.size());
            Hooks.scenario.log("Total number of records in file are :" + allRecordsFromFeed.size());
            Map<List<String>, List<String>> missingInDestination = E2ECompareUtil.getMissingInDestinationAsMap(allRecordsFromFeed, allRecordsFromDB);
            Assert.assertEquals(missingInDestination.size(), 0, "These records are missing in destination " + missingInDestination);
            Map<List<String>, List<String>> extraInDestination = E2ECompareUtil.getExtraInDestinationAsMap(allRecordsFromFeed, allRecordsFromDB);
            Assert.assertEquals(extraInDestination.size(), 0, "These records are extra in destination " + extraInDestination);
        }
    }


    //Tested
    @When("I verify all record of csv feed file to db")
    public void csvFeedAndDBAssertionsForAllRecord(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            Map<List<String>, List<String>> allRecordsListDB = DBCommonUtils.getAllRecordsByQueryAsMap("Select * from " + tableName);
            Map<List<String>, List<String>> allRecordsFeed = CSVFileOperationUtil.getAllRecordAsMap(fileName);
            assert allRecordsListDB != null;
            Map<List<String>, List<String>> result = E2ECompareUtil.compareTwoMap(allRecordsListDB, allRecordsFeed);
            Assert.assertEquals(result.size(), 0, "These records are diff " + result);
        }
    }

    //Tested
    @When("I verify specific record of csv feed file to db")
    public void csvFeedAndDBAssertionsForSpecificAssertion(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String filterKey = column.get("filterKey");
            String filterValue = column.get("filterValue");
            HashSet<String> specificRecordDB = DBCommonUtils.getSpecificRecord(tableName, filterKey, filterValue);
            HashSet<String> specificRecordFeed = CSVFileOperationUtil.getSpecificRecord(fileName, filterValue);
            assert specificRecordDB != null;
            boolean allMatch = specificRecordFeed.containsAll(specificRecordDB);
            Assert.assertTrue(allMatch);
        }
    }

    //Tested
    @When("I verify the record count of csv feed file to db")
    public void feedAndDBAssertions(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            int totalRecordCountForTableOrView = DBCommonUtils.getTotalRecordCountForTable(tableName);
            int totalRecordCountFromFeed = CSVFileOperationUtil.getRecordCount(fileName);
            Hooks.scenario.log("DB Record Count for " + tableName + " " + totalRecordCountForTableOrView);
            Hooks.scenario.log("Feed Record Count for " + " " + totalRecordCountFromFeed);
            Assert.assertEquals(totalRecordCountFromFeed, totalRecordCountForTableOrView);
        }
    }
}
