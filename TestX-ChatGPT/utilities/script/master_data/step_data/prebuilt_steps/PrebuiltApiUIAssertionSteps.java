package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.jayway.jsonpath.JsonPath;
import com.testx.web.api.selenium.restassured.qe.ui.config.Configuration;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import org.json.JSONException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class PrebuiltApiUIAssertionSteps extends BaseSetup {

    private static final String API_RESPONSE_PATH="src/test/resources/api_response_data/";

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltApiUIAssertionSteps.class);
    public static Configuration configuration = ConfigurationManager.getConfiguration();
    TestContext testContext;

    public PrebuiltApiUIAssertionSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

//    /**
//     * StepDef to validate the UI data to API data
//     */
//
    @Then("I verify the data of below element to actual {string} data on the {string} page")
    public void iVerifyUIDataToAPIData(String api, String pageName,DataTable usertable) throws IOException, JSONException {

        String api_json_path="";
        String elementName="";
        List<Map<String, String>> data = usertable.asMaps(String.class, String.class);
        for (Map<String, String> form : data) {
            api_json_path = form.get("apiPath");
            elementName = form.get("uiElement");
        }
        WebElement element = loadWebElement(elementName, pageName);
        String uidata=element.getText();
        String responsepath = Files.readString(Path.of(API_RESPONSE_PATH + api + ".json"));
        Object document = com.jayway.jsonpath.Configuration.defaultConfiguration().jsonProvider().parse(responsepath);

        String apidata = JsonPath.read(document, api_json_path).toString();
        System.out.println("apidata"+apidata);
        System.out.println("uidata "+uidata);
        Assert.assertEquals(uidata,apidata);
    }
}