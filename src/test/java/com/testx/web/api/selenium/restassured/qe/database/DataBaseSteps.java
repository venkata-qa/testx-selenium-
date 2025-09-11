package com.testx.web.api.selenium.restassured.qe.database;

import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseSteps {

    public static String predicted_Options;
    public static String dbValue;
    public static List<Map<String, String>> predictedListFromDB = new ArrayList<>();
    public static Connection connection;
    public static List<Map<String, String>> recordList = new ArrayList<>();
    public static ResultSet resultSet;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseSteps.class);


    @When("I create database {string}")
    public void createDatabase(String databaseName) {
        try {
            connection = DriverManager.getConnection
                    (ConfigurationManager.getConfiguration().dbUrl()+"/?user="+ConfigurationManager.getConfiguration().dbUser()+"&password="+ConfigurationManager.getConfiguration().dbPassword());
            int Result=connection.createStatement().executeUpdate("CREATE DATABASE "+databaseName);
            LOGGER.info(">>>Result Value>>>"+Result);
            Assert.assertEquals(Result,1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @When("I create table {string} in database")
    public void createTable(String tableName,List<Map<String,String>> data){
      String columnDetails=  data.get(0).get("columnDetails");
        int Result= 0;
        try {
            Result = connection.createStatement().executeUpdate("CREATE TABLE  "+tableName +"("+columnDetails+");");
            LOGGER.info(">>>Result Value>>>"+Result);
            Assert.assertEquals(Result,0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @When("I establish database connection")
    public void establishDataBaseConnection(List<Map<String, String>> list){
        LOGGER.info("DataBase connection establish initiated...");
        try {
            Class.forName(list.get(0).get("driverName"));
            connection = DriverManager.getConnection(list.get(0).get("db_url"), list.get(0).get("username"),
                    list.get(0).get("password"));
            LOGGER.info("DataBase connection established successfully...");
        } catch (SQLException e) {
            // e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
    }

    @When("I establish the database connection")
    public void establishTheDataBaseConnection(){
        LOGGER.info("Database connection establish initiated...");
        try {
            Class.forName(ConfigurationManager.getConfiguration().dbDriver());
            connection = DriverManager.getConnection(ConfigurationManager.getConfiguration().dbUrlWithDatabaseName(), ConfigurationManager.getConfiguration().dbUser(),
                    ConfigurationManager.getConfiguration().dbPassword());
            LOGGER.info("Database connection established successfully...");
        } catch (SQLException e) {
            // e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // e.printStackTrace();
        }
    }

    @And("I execute the query")
    public void prepareAndExecutedQuery(List<Map<String, String>> list){
        LOGGER.info("Execute the database query");
        int Result ;
        String query, where, orderBy, set, columnName, columnValue;
        for(int i=0;i<list.size();i++) {
            switch (list.get(i).get("queryType").toString().toLowerCase()) {
                case "select":
                    query = list.get(i).get("queryType") + " " + list.get(i).get("selectedColumnName") + " from " + list.get(i).get("tableName");
                    where = StringUtils.isNotBlank(list.get(i).get("condition")) ? " where " + list.get(i).get("condition") : "";
                    orderBy = StringUtils.isNotBlank(list.get(i).get("orderBy")) ? " order by " + list.get(i).get("orderBy") : "";
                    System.out.println("Prepare select statement : " + query + where + orderBy + ";");
                    try {
                        resultSet = connection.createStatement().executeQuery(query + where + orderBy + ";");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "update":
                    query = list.get(i).get("queryType") + " " + list.get(i).get("tableName");
                    set = StringUtils.isNotBlank(list.get(i).get("setColumnValue")) ? " set " + list.get(i).get("setColumnValue") : "";
                    where = StringUtils.isNotBlank(list.get(i).get("condition")) ? " where " + list.get(i).get("condition") : "";
                    System.out.println("Prepare update statement : " + query + set + where+ ";");
                    try {
                        Result = connection.createStatement().executeUpdate(query + set + where+ ";");
                        Assert.assertEquals(Result, 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "delete":
                    query = list.get(i).get("queryType") + " " + " from " + list.get(i).get("tableName");
                    where = StringUtils.isNotBlank(list.get(i).get("condition")) ? " where " + list.get(i).get("condition") : "";
                    System.out.println("Prepare delete statement : " + query + where+";");
                    try {
                        Result = connection.createStatement().executeUpdate(query + where+";");
                        Assert.assertEquals(Result, 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "insert":
                    query = list.get(i).get("queryType") + " " + " into " + list.get(i).get("tableName");
                    columnName = StringUtils.isNotBlank(list.get(i).get("columnName")) ? " (" + list.get(i).get("columnName") + ")" : "";
                    columnValue = StringUtils.isNotBlank(list.get(i).get("columnValue")) ? " values (" + list.get(i).get("columnValue") + ")" : "";
                    System.out.println("Prepare delete statement : " + query + columnName + columnValue + ";");
                    try {
                        Result = connection.createStatement().executeUpdate(query + columnName + columnValue + ";");
                        Assert.assertEquals(Result, 1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    System.out.println("Provide db operation is not supported !!!");
            }
        }
    }

    @And("I select schema name as {string}")
    public void selectSchema(String schemaName){
        LOGGER.info("Selected schema name as " + schemaName);
        try {
            connection.setSchema(schemaName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I select the schema name")
    public void selectTheSchema() {
        LOGGER.info("Select schema name as " + ConfigurationManager.getConfiguration().dbSchema());
        try {
            connection.setSchema(ConfigurationManager.getConfiguration().dbSchema());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I execute the query {string}")
    public void executeQuery(String query) {
        LOGGER.info("Executed the database query");
        try {
            resultSet = connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @And("I save database result")
    public void saveResult(List<Map<String, String>> list){
        LOGGER.info("Save the result");
        Map<String, String> recordMap = new HashMap<>();
        ResultSetMetaData resultSetMetaData = null;
        try {
            resultSetMetaData = resultSet.getMetaData();
            if (resultSetMetaData.getColumnCount() == list.size()) {
                while (resultSet.next()) {
                    for (int i = 0; i < list.size(); i++) {
                        recordMap.put(list.get(i).get("columnName"), resultSet.getString(list.get(i).get("columnName")));
                    }
                    recordList.add(recordMap);
                    LOGGER.info("Result saved successfully");
                }
            } else {
                LOGGER.error("Found differences in column names !!!!");
                Assert.fail("Found differences in column names !!!!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @And("I close database connection")
    public void closeConnection() {
        LOGGER.info("Connection closed successfully");
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
