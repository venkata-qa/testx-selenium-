
package com.testx.web.api.selenium.restassured.qe.util.dbutils;

import com.testx.web.api.selenium.restassured.qe.config.DBConfig;
import lombok.extern.slf4j.Slf4j;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Slf4j
public class DBCommonUtils {

    private static final Logger log = LoggerFactory.getLogger(DBCommonUtils.class);

    //Tested
    public static List<String> getColumnsName(String tableName){
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        List<String> colList = new ArrayList<>();
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "select * from " + tableName;
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                colList.add(metaData.getColumnName(i));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return colList;
    }

    //Tested
    public static List<String> getSpecificRecordAsList(String tableName, String filterKey, String filterValue){
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "select * from " + tableName + " where " + filterKey + "=" + filterValue;
            resultSet = statement.executeQuery(query);
            List<String> values = new ArrayList<>();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> sortedRecord = null;
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (resultSet.getString(i) == null)
                        values.add("");
                    else
                        values.add(resultSet.getString(i));
                }
                sortedRecord = new ArrayList<>(values);
            }
            return sortedRecord;
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    //Tested
    public static HashSet<String> getSpecificRecord(String tableName, String filterKey, String filterValue){
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "select * from " + tableName + " where " + filterKey + "='" + filterValue + "'";
            resultSet = statement.executeQuery(query);
            List<String> values = new ArrayList<>();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            HashSet<String> sortedRecord = null;
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (resultSet.getString(i) == null)
                        values.add("");
                    else
                        values.add(resultSet.getString(i));
                }
                sortedRecord = new HashSet<>(values);
            }
            return sortedRecord;
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    //Tested
    public static List<String> getSpecificColumnForAllRecords(String tableName, String columnName){
        if (tableName.contains("Blank")) {
            tableName = tableName.replace("Blank", "");
        }
        List<String> allRecords = new ArrayList<>();
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "select " + columnName + " from " + tableName;
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                allRecords.add(resultSet.getString(columnName));
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return allRecords;
    }

    //Tested
    public static Map<List<String>, List<String>> getAllRecordsByQueryAsMap(String query){
        Map<List<String>, List<String>> map = new HashMap<>();
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (resultSet.next()) {
                List<String> currentRecordList = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    if (resultSet.getString(i) == null)
                        currentRecordList.add("");
                    else if (resultSet.getString(i).contains("00:00:00")) {
                        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //This format in db
                        SimpleDateFormat out = new SimpleDateFormat("dd-MMM-yy"); //But in file we required this format
                        Date date = in.parse(resultSet.getString(i));
                        currentRecordList.add(out.format(date).toUpperCase(Locale.ROOT));
                    } else
                        currentRecordList.add(resultSet.getString(i));
                }
                map.put(currentRecordList, currentRecordList);
            }
            return map;
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return null;
    }

    //Tested
    public static int getTotalRecordCountForTable(String tableName) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "select count(*) as count from " + tableName;
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return Integer.parseInt(resultSet.getString("count"));
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                // e.printStackTrace();
            }
        }
        return 0;
    }

    public static Map<String, List<String>> getTwoColumnAsMapFromColumnIndexesList(String query, String columnIndex1, String columnIndex2) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        Map<String, List<String>> map = new HashMap<>();
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getString(Integer.parseInt(columnIndex2)) != null) {
                    if (!map.containsKey(resultSet.getString(Integer.parseInt(columnIndex1)))) {
                        List<String> list = new ArrayList<>();
                        list.add(resultSet.getString(Integer.parseInt(columnIndex2)));
                        map.put(resultSet.getString(Integer.parseInt(columnIndex1)), list);
                    } else {
                        List<String> currentlist = map.get(resultSet.getString(Integer.parseInt(columnIndex1)));
                        currentlist.add(resultSet.getString(Integer.parseInt(columnIndex2)));
                        map.put(resultSet.getString(Integer.parseInt(columnIndex1)), currentlist);
                    }
                } else {
                    map.put(resultSet.getString(Integer.parseInt(columnIndex1)), new ArrayList<>());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static List<String> getTwoColumnAsList(String query, String columnName1, String columnName2) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        List<String> list = new ArrayList<>();
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                list.add(resultSet.getString(columnName1) == null ? "blank" : resultSet.getString(columnName1));
                list.add(resultSet.getString(columnName2) == null ? "blank" : resultSet.getString(columnName2));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static Map<String, String> getTwoColumnAsMap(String query, String columnName1, String columnName2) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        Map<String, String> map = new HashMap<>();
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                if (resultSet.getString(columnName2) != null)
                    map.put(resultSet.getString(columnName1), resultSet.getString(columnName2).trim());
                else
                    map.put(resultSet.getString(columnName1), "blank");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    public static Set<String> getDuplicateRecords(String query) {
        Set<String> uniqueRecord = new HashSet<>();
        Set<String> duplicateRecord = new HashSet<>();
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                List<String> values = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    values.add(resultSet.getString(i));
                }
                if (!uniqueRecord.add(String.join("", values)))
                    duplicateRecord.addAll(values);
            }
            log.info("Total number of unique records in table is: " + uniqueRecord.size());
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return duplicateRecord;
    }

    public static String getColumnValue(String query, String columnName) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getString(columnName);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return null;
    }

    public static LinkedList<String> getPrimaryKeysOfTable(String tableName) {
        Connection conn = DBConfig.getConnection();
        DatabaseMetaData databaseMetaData;
        LinkedList<String> primaryKeyList = new LinkedList<>();
        try {
            databaseMetaData = conn.getMetaData();
            ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, tableName);
            while (primaryKeys.next()) {
                String pkColumnName = primaryKeys.getString("COLUMN_NAME");
                primaryKeyList.add(pkColumnName);
            }
            return primaryKeyList;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return primaryKeyList;
    }

    public static int updateQuery(String query) {
        int result = 0;
        Statement statement;
        Connection conn = DBConfig.getConnection();
        try {
            statement = conn.createStatement();
            result = statement.executeUpdate(query);
            statement.executeUpdate("COMMIT");
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return result;
    }

    public static boolean isValueInTable(String tableName, String columnName, String value) {
        Connection conn = DBConfig.getConnection();
        Statement statement;
        ResultSet resultSet;
        boolean flag = false;
        int rowCount = 0;
        try {
            statement = conn.createStatement();
            statement.setFetchSize(1000);
            String query = "Select * from "+tableName+" where "+columnName+" ="+value;
            resultSet = statement.executeQuery(query);
            if (resultSet.next())
                rowCount = Integer.parseInt(resultSet.getString("COUNT(*)"));
            if (rowCount >= 1) {
                flag = true;
            }
            return flag;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public static Map<String, String> checkMapValueInTable(String query, Map<String, String> result){
        Connection conn;
        ResultSet resultSet;
        Map<String, String> missingValues = new HashMap<>();
        for (Map.Entry<String, String> entry : result.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            conn = DBConfig.getConnection();
            try {
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setFetchSize(1000);
                preparedStatement.setString(1, key);
                preparedStatement.setString(2, value);
                resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    missingValues.put(key, value);
                }
            }
            catch (Exception e){
                // e.printStackTrace();
            }
            return missingValues;
        }
        return missingValues;
    }
}