
package com.testx.web.api.selenium.restassured.qe.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UploadAndDownloadJsonFromSnowflake {

    public static void main(String[] args) {
        String account = "your_account_name";
        String user = "your_user_name";
        String password = "your_password";
        String warehouse = "your_warehouse_name";
        String database = "your_database_name";
        String schema = "your_schema_name";
        String pathToJSON = "src/test/resources/players.json";
        String stageName = "your_stage_name";
        String fileName = "players.json";

        try {
            // Create a JDBC connection to Snowflake
            Connection conn = SnowFlakeDemo.getConnection();

            Statement stmt = conn.createStatement();

            // Create a stage to upload the JSON file to
//            String createStageCommand = "CREATE STAGE " + stageName;
//            stmt.execute(createStageCommand);
//
//            // Upload the JSON file to the stage using the PUT command
//            String putCommand = "PUT file://" + pathToJSON + " @" + stageName + "/" + fileName;
//            stmt.execute(putCommand);

            // Download the JSON file from the stage using the GET command
            String getCommand = "SELECT GET (@" + stageName + "/" + fileName + ") FROM "+stageName;


            InputStream is = stmt.executeQuery(getCommand).getBinaryStream(0);
            FileOutputStream fos = new FileOutputStream(new File(pathToJSON));
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();

            // Close the JDBC connection
            conn.close();
        } catch (SQLException | IOException e) {
            // e.printStackTrace();
        }
    }

}
