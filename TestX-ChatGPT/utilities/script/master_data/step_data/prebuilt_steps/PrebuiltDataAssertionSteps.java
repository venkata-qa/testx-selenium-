package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.ui.config.Configuration;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.custom_exceptions.InvalidCommonStepSelectionException;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrebuiltDataAssertionSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltDataAssertionSteps.class);
    public static Configuration configuration = ConfigurationManager.getConfiguration();
    TestContext testContext;

    public PrebuiltDataAssertionSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    @Then("^I verify that the text: (.*) (exactly|partially) matches the text of the (.*) field on the (.*)$")
    public void verifyTheWebElementText(String expectedText, String assertionType, String elementName, String pageClassName) {
        String actualText = driverManagerUtils.getElementText(driver, loadWebElement(elementName, pageClassName));
        switch (assertionType) {
            case "exactly":
                assertThat("Element text not matching", actualText, is(equalTo(expectedText)));
                break;
            case "partially":
                assertThat("Element text not matching partially", actualText, containsString(expectedText));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }

    @Then("^I verify that the following text should (exactly|partially) matches the text of the (.*) field on the (.*)$")
    public void verifyTheWebElementTextUsingDT(String assertionType, String elementName, String pageClassName, DataTable dataTable) {
        List<List<String>> data = dataTable.asLists();
        String expectedText = data.get(0).get(0);
        String actualText = driverManagerUtils.getElementText(driver, loadWebElement(elementName, pageClassName));

        switch (assertionType) {
            case "exactly":
                assertThat("Element text not matching", actualText, is(equalTo(expectedText)));
                break;
            case "partially":
                assertThat("Element text not matching partially", actualText, containsString(expectedText));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }

    /**
     * Step to check the text of the web elements on the page using data table
     *
     * @param pageClassName page class name of the fields
     * @param dataTable     to pass the web elements, and their expected text on tabular form
     */
    @When("I verify the text of the following fields on the {string}")
    public void iVerifyTheTextOfTheFieldsOnThePage(String pageClassName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        SoftAssertions softly = null;
        for (Map<String, String> column : rows) {
            softly = new SoftAssertions();
            WebElement webElement = loadWebElement(column.get("fieldName"), pageClassName);
            String actualValue = driverManagerUtils.getElementText(driver, webElement);
            String expectedValue = column.get("fieldText");
            softly.assertThat(actualValue).as("Element text not matching").isEqualTo(expectedValue);
        }
        assert softly != null;
        softly.assertAll();
    }


    @Then("^I verify that the text: \"(.*)\" (exactly|partially) matches the current page title$")
    public void verifyThePageTitle(String expectedPageTitle, String assertionType) {
        String actualPageTitle = driverManagerUtils.getPageTitle();
        switch (assertionType) {
            case "exactly":
                assertThat("Page title is not matching", actualPageTitle, is(equalTo(expectedPageTitle)));
                break;
            case "partially":
                assertThat("Page tile is not matching", actualPageTitle, containsString(expectedPageTitle));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }

    @Then("^I verify that the following text should (exactly|partially) matches the page title$")
    public void verifyThePageTitleUsingDT(String assertionType, DataTable dataTable) {
        List<List<String>> data = dataTable.asLists();
        String expectedPageTitle = data.get(1).get(0);
        String actualPageTitle = driverManagerUtils.getPageTitle();

        switch (assertionType) {
            case "exactly":
                assertThat("Page titles are not matching", actualPageTitle, is(equalTo(expectedPageTitle)));
                break;
            case "partially":
                assertThat("Page title does not contains the expected value", actualPageTitle, containsString(expectedPageTitle));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }
}