
package com.testx.web.api.selenium.restassured.qe.util.feedutils;


import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.Hooks;
import com.testx.web.api.selenium.restassured.qe.util.ResourceReaderUtil;
import com.testx.web.api.selenium.restassured.qe.util.dbutils.DBCommonUtils;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.util.*;
@Log4j
public class TextFileOperationUtil {

    //Tested
    public static int getColumnCount(String fileName) {
        BufferedReader bufferReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        try {
            String[] value = bufferReader.readLine().split(",", -1);
            return value.length;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Tested
    public static List<String> getSpecificRecord(BufferedReader bufferReader, String userID) {
        String line;
        try {
            while ((line = bufferReader.readLine()) != null) {
                if (line.contains(userID)) {
                    String[] value = line.split(",");
                    return new ArrayList<>(Arrays.asList(value));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Tested
    public static Map<List<String>, List<String>> getRecordFromFileAsMap(String fileName) {
        BufferedReader bufferReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        Map<List<String>, List<String>> allRecord = new HashMap<>();
        String line;
        try {
            while ((line = bufferReader.readLine()) != null) {

                String[] value = line.split(",");
                allRecord.put(Arrays.asList(value), Arrays.asList(value));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return allRecord;
    }

    //Tested
    public static List<String> getSpecificColData(String fileName, int indexOfColumn) {
        BufferedReader bufferedReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        List<String> allRecord = new ArrayList<>();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] value = line.split(",", -1);
                allRecord.add(value[indexOfColumn]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Hooks.scenario.log("Specific column data for "+fileName +" with column index " +indexOfColumn + ": "+allRecord);
        return allRecord;
    }

    //Tested
    public static int getRecordCount(String fileName) {
        BufferedReader bufferedReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        int count = 0;
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("#")) {
                    count = count - 1;
                } else {
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    //Tested-main
    public static Map<List<String>, List<String>> getColumnlWiseRecordFromFileAsMap(String fileName, int index) {
        BufferedReader br =null;
        Map<List<String>, List<String>> allRecord = new HashMap<>();
        try {
            br = ResourceReaderUtil.getBufferReaderStream(fileName);
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(",", -1);
                allRecord.put(Collections.singletonList(value[index]), Collections.singletonList(value[index]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Ensure the BufferedReader is closed
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    // Handle potential IOException from close()
                    ex.printStackTrace(); // Or handle it as needed
                }
            }
        }
        return allRecord;
    }

    //Tested-main
    public static Set<String> getDuplicateRecords(String fileName) {
        BufferedReader bufferedReader = ResourceReaderUtil.getBufferReaderStream(fileName);
        Set<String> uniqueRecord = new HashSet<>();
        Set<String> duplicateRecord = new HashSet<>();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (!uniqueRecord.add(line)) {
                    duplicateRecord.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duplicateRecord;
    }

    //Tested
    public static boolean getSpecificRecordAndCompare(String fileName, String tableName, String filterKey, String filterValue){
        BufferedReader bufferReaderStream = ResourceReaderUtil.getBufferReaderStream(fileName);
        List<String> fileRecord = TextFileOperationUtil.getSpecificRecord(bufferReaderStream, filterValue);
        List<String> databaseRecord = DBCommonUtils.getSpecificRecordAsList(tableName, filterKey, filterValue);
        log.info(fileRecord);
        log.info(databaseRecord);
        boolean flag = false;
        assert fileRecord != null;
        for (String value : fileRecord) {
            assert databaseRecord != null;
            flag = databaseRecord.contains(value);
            if (!flag) {
                return false;
            }
        }
        return flag;
    }
}