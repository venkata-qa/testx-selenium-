
package com.testx.web.api.selenium.restassured.qe.backend.stepdefs;

import com.testx.web.api.selenium.restassured.qe.config.DBConfig;
import com.testx.web.api.selenium.restassured.qe.util.E2ECompareUtil;
import com.testx.web.api.selenium.restassured.qe.util.dbutils.DBCommonUtils;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.log4j.Log4j;
import org.testng.Assert;
import org.testng.SkipException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Log4j
public class DBAssertions {

    String sqlFilePath = "src/test/resources/properties/sp.sql";
    String procedureName = "testXDatabaseSetup";

    //Tested
    @When("I compare all records of target table with view table")
    public void dataVerificationTargetToViewTable(DataTable dataTable){
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> column : data) {
            String sourceQuery = column.get("sourceQuery");
            String targetQuery = column.get("targetQuery");

            Map<List<String>, List<String>> allRecordsInTarget = DBCommonUtils.getAllRecordsByQueryAsMap(targetQuery);
            assert allRecordsInTarget != null;

            Map<List<String>, List<String>> allRecordsInSource = DBCommonUtils.getAllRecordsByQueryAsMap(sourceQuery);
            assert allRecordsInSource != null;

            Map<List<String>, List<String>> diffList = E2ECompareUtil.compareTwoMap(allRecordsInTarget, allRecordsInSource);
            Assert.assertEquals(diffList.size(), 0);
        }
    }

    @Then("Delete the created procedure")
    public void deleteProcedure() {
        try (Connection connection = DBConfig.getConnection()) {
            String dropProcedureQuery = "DROP PROCEDURE IF EXISTS " + procedureName;
            try (PreparedStatement dropStatement = connection.prepareStatement(dropProcedureQuery)) {
                dropStatement.execute();
                System.out.println("Stored procedure " + procedureName + " deleted successfully.");
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }
    }

    @When("Create and execute procedure")
    public void procedureSetup() {
        try (Connection connection = DBConfig.getConnection()) {
            String checkProcedureQuery = "SELECT COUNT(*) AS procedure_exists " +
                    "FROM information_schema.routines " +
                    "WHERE routine_schema = ? AND routine_name = ? AND routine_type = 'PROCEDURE'";

            try (PreparedStatement checkStatement = connection.prepareStatement(checkProcedureQuery)) {
                checkStatement.setString(1, connection.getCatalog()); // Current database name
                checkStatement.setString(2, procedureName);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                int procedureExists = resultSet.getInt("procedure_exists");
                if (procedureExists == 0) {
                    StringBuilder sqlScript = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sqlScript.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("Error reading SQL file: " + e.getMessage());
                    }

                    try (PreparedStatement statement = connection.prepareStatement(sqlScript.toString())) {
                        statement.execute();
                        System.out.println("Stored procedure executed successfully.");
                    }

                    String procedureCall = "CALL " + procedureName + "()";
                    try (PreparedStatement callStatement = connection.prepareStatement(procedureCall)) {
                        callStatement.execute();
                        System.out.println("Stored procedure executed via CALL statement.");
                    }

                } else System.out.println("Procedure is already available and setup");
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("Database Connection Error: " + e.getMessage());
        }
    }

    //Tested
    @When("I validate the data length for all columns of table")
    public void lengthAndRequiredRule(DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : data) {
            String tableName = column.get("tableName");
            String minLength = column.get("minLength");
            String maxLength = column.get("maxLength");
            String columnName = column.get("columnName");
            String valueType = column.get("valueType");

            List<String> allRecord = DBCommonUtils.getSpecificColumnForAllRecords(tableName, columnName);

            int count = 0;
            for (String current : allRecord) {
                if (valueType.equals("Required") || valueType.equals("Optional")) {
                    try {
                        int length = current.length();
                        boolean result = length >= Integer.parseInt(minLength) && length <= Integer.parseInt(maxLength);
                        Assert.assertTrue(result, "This value size is not as per requirement for " + columnName + " column, Which having value " + current);
                        Assert.assertFalse(current.equals(null));
                    } catch (Exception e) {
                        count++;
                    }
                } else
                    Assert.assertThrows(NullPointerException.class, () -> current.length());
                if (allRecord.size() == count)

                    throw new SkipException("All records having null value for " + columnName + " So will not able to validate the length");
            }
        }
    }
}