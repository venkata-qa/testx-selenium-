package com.testx.web.api.selenium.restassured.qe.util.jira;

import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JIRAClient {

    public static String createBug(String summary, String description) throws JSONException, IOException {
        String payload = new String(Files.readAllBytes(Paths.get("src/test/resources/BugFormat/bug.json")));
        Map<String, Object> queryString = new HashMap<>();
        queryString.put("updateHistory", true);
        queryString.put("applyDefaultValues", false);
        String auth = ConfigurationManager.getConfiguration().jiraUserName() + ":" + ConfigurationManager.getConfiguration().jiraToken();
        Response res =RestAssured.given()
                .log().all()
                .headers("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()))
                .contentType("application/json")
                .body(payload.replaceAll("SUMMARY", "Automation : " + summary)
                        .replaceAll("DESCRIPTION", description))
                .post(ConfigurationManager.getConfiguration().jiraURL()+"/rest/api/2/issue");
        String bugId=res.jsonPath().getString("key");
        System.out.println("TestX_Xray - Bug "+res.jsonPath().getString("key")+" created successfully...");
        return bugId;
    }

    public static void attachScreenshotInBug(String issueKey, File file) throws JSONException, IOException {
        String auth = ConfigurationManager.getConfiguration().jiraUserName() + ":" + ConfigurationManager.getConfiguration().jiraToken();
        Response response = RestAssured.given()
                .headers("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()))
                .contentType("multipart/form-data")
                .header("X-Atlassian-Token", "no-check") // Required for JIRA
                .multiPart("file", file) // Attach the file
                .post(ConfigurationManager.getConfiguration().jiraURL()+"/rest/api/2/issue/" + issueKey + "/attachments");
        System.out.println("TestX_Xray - Screenshot Attached successfully...");
    }


}

