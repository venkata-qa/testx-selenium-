
package com.testx.web.api.selenium.restassured.qe.util.feedutils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.testx.web.api.selenium.restassured.qe.util.ResourceReaderUtil;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class CSVFileOperationUtil {

    //Tested
    public static List<String> getSpecificColumnAllValues(String fileName, String columnIndex) {
        CSVReader reader = ResourceReaderUtil.readCSVFile(fileName);
        List<String[]> rows = null;
        List<String> valueToReturn = new ArrayList<>();
        try {
            rows = reader.readAll();
        } catch (IOException | CsvException e) {
            // e.printStackTrace();
        }
        // Find the index of the record we want
        assert rows != null;
        for (String[] row : rows) {
            // start at index 1 to exclude the header row
            valueToReturn.add(row[Integer.parseInt(columnIndex)]);
        }
        return valueToReturn;
    }

    //Tested
    public static HashSet<String> getSpecificRecord(String fileName, String recordId) {
        CSVReader reader = ResourceReaderUtil.readCSVFile(fileName);
        List<String[]> rows = null;
        try {
            rows = reader.readAll();
        } catch (Exception e) {
            // e.printStackTrace();
        }
        int recordIndex = -1;

        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row[0].equals(recordId)) {
                recordIndex = i;
                break;
            }
        }
        String[] record = null;
        if (recordIndex == -1) {
            System.err.println("Record not found");
        } else {
            record = rows.get(recordIndex);
        }

        log.info(System.currentTimeMillis());
        assert record != null;
        return Arrays.stream(record).collect(Collectors.toCollection(HashSet::new));
    }

    //Tested
    public static int getRecordCount(String file) {
        CSVReader reader = ResourceReaderUtil.readCSVFile(file);
        List<String[]> allData = null;
        try {
            allData = reader.readAll();
        } catch (IOException | CsvException el) {
            el.printStackTrace();
        }
        assert allData != null;
        return allData.size();
    }

    //Tested
    public static Map<List<String>, List<String>> getAllRecordAsMap(String fileName) {
        CSVReader reader = ResourceReaderUtil.readCSVFile(fileName);
        Map<List<String>, List<String>> allRecords = new HashMap<>();
        List<String[]> rows = null;
        try {
            rows = reader.readAll();
        } catch (IOException | CsvException e) {
            // e.printStackTrace();
        }
        assert rows != null;
        for (String[] row : rows) {
            allRecords.put(Arrays.stream(row).collect(Collectors.toList()), Arrays.stream(row).collect(Collectors.toList()));
        }
        return allRecords;
    }
}