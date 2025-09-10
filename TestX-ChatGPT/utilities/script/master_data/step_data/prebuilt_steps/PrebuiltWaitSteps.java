package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class PrebuiltWaitSteps extends BaseSetup {

    WebDriver driver;
    TestContext testContext;

    public PrebuiltWaitSteps(TestContext context) {
        super(context);
        this.testContext = context;
        driver = testContext.getDriverManager().getDriver();
    }

    /** Wait for an element to be present for a specific period of time */
    @When("^I wait for element (.*) to be present on the (.*)")
    public void waitForElementPresent(String elementName,String pageClassName) throws Exception
    {
        driverManagerUtils.waitForElementPresent(loadWebElement(elementName, pageClassName));
    }

    /**Wait for an element to be visible for a specific period of time */
    @When("^I wait element (.*?) to be visible on the (.*)")
    public void waitForElementVisible(String elementName, String pageClassName) throws Exception
    {
        driverManagerUtils.waitForElementVisible(loadWebElement(elementName, pageClassName));
    }

    @When("I wait {string} seconds to synchronize the things on app")
    public void waitForSomeTime(String time) throws Exception
    {
        int i = Integer.parseInt(time);
        Thread.sleep(i*1000);
    }
}
