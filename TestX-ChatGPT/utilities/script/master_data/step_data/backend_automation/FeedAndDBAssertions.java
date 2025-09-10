
package com.testx.web.api.selenium.restassured.qe.backend.stepdefs;

import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.Hooks;
import com.testx.web.api.selenium.restassured.qe.util.E2ECompareUtil;
import com.testx.web.api.selenium.restassured.qe.util.dbutils.DBCommonUtils;
import com.testx.web.api.selenium.restassured.qe.util.feedutils.TextFileOperationUtil;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import lombok.extern.java.Log;
import org.testng.Assert;
import org.testng.SkipException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@Log
public class FeedAndDBAssertions {
    //Tested
    @When("I validate the data length for all columns of feed file")
    public void lengthAndRequiredRule(DataTable dataTable) {
        Hooks.scenario.log("Start -: I validate the data length for all columns of feed file");
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String minLength = column.get("minLength");
            String maxLength = column.get("maxLength");
            String fileColumnIndex = column.get("fileColumnIndex");
            String valueType = column.get("valueType");

            List<String> fileData = TextFileOperationUtil.getSpecificColData(fileName, Integer.parseInt(fileColumnIndex));

            int count = 0;
            for (String current : fileData) {
                if (valueType.equals("Required") || valueType.equals("Optional")) {
                    try {
                        int length = current.length();
                        boolean result = length >= Integer.parseInt(minLength) && length <= Integer.parseInt(maxLength);
                        Assert.assertTrue(result, "This value size is not as per requirement for " + fileColumnIndex + " column, Which having value " + current);
                        Assert.assertFalse(current.equals(null));
                    } catch (Exception e) {
                        count++;
                    }
                } else
                    Assert.assertThrows(NullPointerException.class, () -> current.length());
                if (fileData.size() == count)

                    throw new SkipException("All records having null value for " + fileColumnIndex + " So will not able to validate the length");
            }
        }
    }

    //Tested
    @When("I validated records are sorted on the basis of {string} in table {string}")
    public void sortingOfColumn(String columnName, String tableName){
        List<String> specificColumnForAllRecords = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
        List<String> specificColumnForAllRecordAfterSorting = specificColumnForAllRecords.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(specificColumnForAllRecordAfterSorting, specificColumnForAllRecords);
    }

    //Tested
    @When("I verify the columns name and count of feed files with db")
    public void columnNameAndCountOfFeedAndDB(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            int dbColumnCount = DBCommonUtils.getColumnsName(tableName).size();
            Hooks.scenario.log(String.valueOf(dbColumnCount));
            int fileColumnCount = TextFileOperationUtil.getColumnCount(fileName);
            Hooks.scenario.log(String.valueOf(fileColumnCount));
            Hooks.scenario.log("Total number of column in DB are : " + dbColumnCount);
            Hooks.scenario.log("Total number of column in file are :" + fileColumnCount);
            Assert.assertEquals(dbColumnCount, fileColumnCount);
        }
    }

    //Tested
    @When("I compare specific record in feed and db")
    public void compareSpecificRecord(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String filterKey = column.get("filterKey");
            String filterValue = column.get("filterValue");
            Assert.assertTrue(TextFileOperationUtil.getSpecificRecordAndCompare(fileName, tableName,
                    filterKey, filterValue));
        }
    }

    //Tested
    @When("I compare all records of feed files with db")
    public void compareAllRecordsOfFeedAndDB(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            Map<List<String>, List<String>> allRecordsInView = DBCommonUtils.getAllRecordsByQueryAsMap("Select * from " + tableName);
            assert allRecordsInView != null;
            Hooks.scenario.log("Total number of records in view table are : " + allRecordsInView.size());
            Map<List<String>, List<String>> allRecordsFromFeed = TextFileOperationUtil.
                    getRecordFromFileAsMap(fileName);
            Hooks.scenario.log("Total number of records in file are :" + allRecordsFromFeed.size());
            Map<List<String>, List<String>> result = E2ECompareUtil.
                    compareTwoMap(allRecordsFromFeed, allRecordsInView);
            Assert.assertEquals(result.size(), 0, "These records are not as per expectation " + result);
        }
    }

    //Tested
    @When("Verify avg value of a specific column in feed file")
    public void avgValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnIndex = column.get("columnIndex");
            String columnName = column.get("columnName");
            List<String> fileData = TextFileOperationUtil.getSpecificColData(fileName,
                    Integer.parseInt(columnIndex));
            List<Integer> colDataForAvgInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double avgValueInFile = colDataForAvgInFile.stream().mapToInt(Integer::intValue).average().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForAvgInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            double avgValueInDB = colDataForAvgInDB.stream().mapToInt(Integer::intValue).average().orElse(0);
            Hooks.scenario.log("The max value is " + avgValueInFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + avgValueInDB + " from " + columnName + " column in DB");
            Assert.assertEquals(avgValueInFile, avgValueInDB);
        }
    }

    //Tested
    @When("Verify sum value of a specific column in feed file")
    public void sumValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnIndex = column.get("columnIndex");
            String columnName = column.get("columnName");
            List<String> fileData = TextFileOperationUtil.getSpecificColData(fileName,
                    Integer.parseInt(columnIndex));
            List<Integer> colDataForAvgInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int sumFromFile = colDataForAvgInFile.stream().mapToInt(Integer::intValue).sum();
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForAvgInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int sumFromDB = colDataForAvgInDB.stream().mapToInt(Integer::intValue).sum();
            Hooks.scenario.log("The max value is " + sumFromFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + sumFromDB + " from " + columnName + " column in DB");
            Assert.assertEquals(sumFromFile, sumFromDB);
        }
    }

    //Tested
    @When("Verify max value of a specific column in feed file")
    public void maxValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnIndex = column.get("columnIndex");
            String columnName = column.get("columnName");
            List<String> fileData = TextFileOperationUtil.getSpecificColData(fileName,
                    Integer.parseInt(columnIndex));
            List<Integer> colDataForAvgInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxFromFile = colDataForAvgInFile.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForAvgInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int maxFromDB = colDataForAvgInDB.stream().mapToInt(Integer::intValue).max().orElse(0);
            Hooks.scenario.log("The max value is " + maxFromFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + maxFromDB + " from " + columnName + " column in DB");
            Assert.assertEquals(maxFromFile, maxFromDB);
        }
    }

    //Tested
    @When("Verify min value of a specific column in feed file")
    public void minValueOfSpecificColumn(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String columnIndex = column.get("columnIndex");
            String columnName = column.get("columnName");
            List<String> fileData = TextFileOperationUtil.getSpecificColData(fileName,
                    Integer.parseInt(columnIndex));
            List<Integer> colDataForAvgInFile = fileData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int minFromFile = colDataForAvgInFile.stream().mapToInt(Integer::intValue).min().orElse(0);
            List<String> dbData = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);
            List<Integer> colDataForAvgInDB = dbData.stream().map(Integer::parseInt).collect(Collectors.toList());
            int minFromDB = colDataForAvgInDB.stream().mapToInt(Integer::intValue).min().orElse(0);
            Hooks.scenario.log("The max value is " + minFromFile + " from " + columnName + " column in file");
            Hooks.scenario.log("The max value is " + minFromDB + " from " + columnName + " column in DB");
            Assert.assertEquals(minFromFile, minFromDB);
        }
    }

    //Tested
    @When("I verify the record count of feed file to db")
    public void feedAndDBAssertions(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String tableName = column.get("tableName");
            String feedSource = column.get("feedSource");
            int totalRecordCountForTableOrView = DBCommonUtils.getTotalRecordCountForTable(tableName);
            int totalRecordCountFromFeed = TextFileOperationUtil.getRecordCount(fileName);
            Hooks.scenario.log("DB Record Count for " + tableName + "totalRecordCountForTableOrView");
            Hooks.scenario.log("Feed Record Count for " + feedSource + " " + totalRecordCountFromFeed);
            Assert.assertEquals(totalRecordCountFromFeed, totalRecordCountForTableOrView);
        }
    }

    //Tested
    @When("I find missing and extra records in db or feed file")
    public void findMissingRecordsOfFeedAndDB(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String fileName = column.get("fileName");
            String fileFormat = column.get("fileFormat");
            String feedSource = column.get("feedSource");
            String tableName = column.get("tableName");
            Map<List<String>, List<String>> allRecordsFromDB = DBCommonUtils.getAllRecordsByQueryAsMap("Select * from " + tableName);
            Map<List<String>, List<String>> allRecordsFromFeed = TextFileOperationUtil.getRecordFromFileAsMap(fileName);

            assert allRecordsFromDB != null;
            Hooks.scenario.log("Total number of records in DB are : " + allRecordsFromDB.size());
            Hooks.scenario.log("Total number of records in file are :" + allRecordsFromFeed.size());
            Map<List<String>, List<String>> missingInDestinationAsMap = E2ECompareUtil.getMissingInDestinationAsMap(allRecordsFromFeed, allRecordsFromDB);
            Assert.assertEquals(missingInDestinationAsMap.size(), 0, "These records are missing in destination " + missingInDestinationAsMap);
            Map<List<String>, List<String>> extraInDestinationAsMap = E2ECompareUtil.getExtraInDestinationAsMap(allRecordsFromFeed, allRecordsFromDB);
            Assert.assertEquals(extraInDestinationAsMap.size(), 0, "These records are extra in destination " + extraInDestinationAsMap);
        }
    }
}