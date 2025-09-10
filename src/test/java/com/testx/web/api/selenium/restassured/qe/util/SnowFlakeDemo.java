package com.testx.web.api.selenium.restassured.qe.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.Properties;

public class SnowFlakeDemo {

    public static Connection getConnection()
            throws SQLException {
        try {
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver not found");
        }
        // build connection properties
        Properties properties = new Properties();
        properties.put("user", "xxx");   // trufflehog:ignore     // replace "" with your username
        properties.put("password", "xxx");  // trufflehog:ignore       // replace "" with your password
        properties.put("account", "xxx");  // replace "" with your account name
        properties.put("db", "xxx");       // replace "" with target database name
        properties.put("schema", "xx");   // replace "" with target schema name
        //properties.put("tracing", "on");

        // create a new connection
        String connectStr = System.getenv("SF_JDBC_CONNECT_STRING");
        // use the default connection string if it is not set in environment
        if (connectStr == null) {
            connectStr = "jdbc:snowflake://bacbwrp-tz26944.snowflakecomputing.com"; // replace accountName with your account name
        }

        // https://bacbwrp-tz26944.snowflakecomputing.com
        return DriverManager.getConnection(connectStr, properties);
    }


    public static Connection getSnowflakeConnection() {
        try {
            Class.forName("net.snowflake.client.jdbc.SnowflakeDriver");
        } catch (ClassNotFoundException ex) {
        }

        Properties properties = new Properties();
// replace with your detail
        properties.put("user", "xxx");// trufflehog:ignore

        properties.put("password", ",xxx");// trufflehog:ignore

        properties.put("role", "xxx");// trufflehog:ignore

        properties.put("warehouse", "xxx");
        properties.put("account", "xxx");
        String connectStr = System.getenv("SF_JDBC_CONNECT_STRING");
// use the default connection string if it is not set in environment
        if (connectStr == null) {
            connectStr = "jdbc: snowflake: //ot46827.eu-west-1.privatelink.snowflakecomputing.com";

        }
        try {
            return DriverManager.getConnection(connectStr, properties);
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }

    static void uplaodJsonFile() {
        try {
            // Create a JDBC connection to Snowflake
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String pathToJSON = "src/test/resources/players.json";
            // Upload the JSON file to Snowflake using the COPY INTO command
            String copyCommand = "COPY INTO " + "players" + " FROM '" + pathToJSON + "' "
                    + "FILE_FORMAT = (TYPE = 'JSON')";
            stmt.execute(copyCommand);

            // Close the JDBC connection
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {

        //processSQL();
        //processNoSQL();
        // getDataFromSnowflake();
        uplaodJsonFile();


    }


    static void getDataFromSnowflake() {
        String sql = "SELECT * FROM PLAYERSINFO";
        String outputfileName = "output.json";
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("LIST @playersinfo");
            // create an output stream to write the file


            while (rs.next()) {
                // get the name of the file
                String fileName = rs.getString("name");
                System.out.println(fileName);
                String database = "DATAMODEL";
                String schema = "JSON_SCHEMA";
                String stage = "PLAYERSINFO";

                // execute the query to download the file
                //ResultSet rs2 = stmt.executeQuery("SELECT GET @playersinfo/" + fileName );
                // String downloadQuery = "GET @" + fileName;
                String downloadQuery = "SELECT GET(@playersinfo/players.json.gz) FROM json_schema";
                System.out.println(downloadQuery);
                ResultSet rs2 = stmt.executeQuery(downloadQuery);

                // create an output stream to write the file
                OutputStream os = new FileOutputStream("/" + fileName);

                // write the file to the output stream
                while (rs2.next()) {
                    os.write(rs2.getBytes(1));
                }

                // close the output stream and result set
                os.close();
                rs2.close();
            }
            // close the output stream, result set, statement, and connection
            //   os.close();
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static void processNoSQL() {
        String sql = "PUT 'file://src/test/resources/players.json' @playersinfo";
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


    static void processSQL() {
        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM employee";
            ResultSet rs = stmt.executeQuery(query);
            String sql = "PUT 'file:/path/to/your/json/file.json' @my_stage";

            while (rs.next()) {
                // int id = rs.getInt("id");
                String name = rs.getString("FIRSTNAME");
                String email = rs.getString("EMAIL");
                System.out.println(", Name: " + name + ", Email: " + email);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

