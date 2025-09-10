package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.ui.config.Configuration;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.java.en.Then;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrebuiltAlertsAssertionSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltAlertsAssertionSteps.class);
    public static Configuration configuration = ConfigurationManager.getConfiguration();
    TestContext testContext;

    public PrebuiltAlertsAssertionSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    /**
     * Step to check that the alert text
     */
    @Then("I should see alert text as \"(.*?)\"")
    public void check_alert_text(String eAlertMessage)
    {
        String aAlertMessage = driverManagerUtils.getAlertText();
        assertThat("Alert messages is not matching",  aAlertMessage, is(eAlertMessage));
    }
}
