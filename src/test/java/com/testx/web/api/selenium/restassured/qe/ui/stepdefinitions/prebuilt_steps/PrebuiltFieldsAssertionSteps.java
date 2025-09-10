
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
import org.testng.Assert;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class PrebuiltFieldsAssertionSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrebuiltFieldsAssertionSteps.class);
    public static Configuration configuration = ConfigurationManager.getConfiguration();
    TestContext testContext;

    public PrebuiltFieldsAssertionSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    @Then("^I verify that the field (.*) should be (visible|invisible) on the (.*)$")
    public void verifyTheWebElementVisibility(String elementName, String assertionType, String pageClassName) {
        boolean isElementDisplayed = driverManagerUtils.isElementDisplayed(loadWebElement(elementName, pageClassName));

        switch (assertionType) {
            case "visible":
                assertThat(elementName + " field should be displayed on the " + pageClassName, isElementDisplayed, is(true));
                break;
            case "invisible":
                assertThat(elementName + " field should not be displayed on the " + pageClassName, isElementDisplayed, is(false));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }

    @Then("^I verify that the following fields should be (visible|invisible) on the (.*)$")
    public void verifyElementsDisplayedOnThePage(String assertionType, String pageClassName, List<String> elementNames) {
        SoftAssertions softly = null;

        switch (assertionType) {
            case "visible":
                for (String elementName : elementNames) {
                    softly = new SoftAssertions();
                    WebElement webElement = loadWebElement(elementName, pageClassName);
                    boolean isElementDisplayed = driverManagerUtils.isElementDisplayed(webElement);
                    softly.assertThat(isElementDisplayed).as("Elements should be displayed").isTrue();
                }
                break;
            case "invisible":
                for (String elementName : elementNames) {
                    softly = new SoftAssertions();
                    WebElement webElement = loadWebElement(elementName, pageClassName);
                    boolean isElementDisplayed = driverManagerUtils.isElementDisplayed(webElement);
                    softly.assertThat(isElementDisplayed).as("Elements should not be displayed").isFalse();
                }
                break;
        }
        assert softly != null;
        softly.assertAll();
    }

    @Then("^I verify that the field (.*) should be (enabled|disabled) on the (.*)$")
    public void verifyTheWebElementEnabledOrDisabled(String elementName, String assertionType, String pageClassName) {
        boolean isElementEnabled = driverManagerUtils.isElementEnabled(loadWebElement(elementName, pageClassName));
        switch (assertionType) {
            case "enabled":
                assertThat(elementName + " field should be enabled on the " + pageClassName, isElementEnabled, is(true));
                break;
            case "disabled":
                assertThat(elementName + " field should be disabled on the " + pageClassName, isElementEnabled, is(false));
                break;
            default:
                throw new InvalidCommonStepSelectionException(assertionType);
        }
    }

    @Then("^I verify that the following fields should be (enabled|disabled) on the (.*)$")
    public void verifyElementsEnabledOrDisabledOnThePage(String assertionType, String pageClassName, List<String> elementNames) {
        SoftAssertions softly = null;

        switch (assertionType) {
            case "enabled":
                for (String elementName : elementNames) {
                    softly = new SoftAssertions();
                    WebElement webElement = loadWebElement(elementName, pageClassName);
                    boolean isElementEnabled = driverManagerUtils.isElementEnabled(webElement);
                    softly.assertThat(isElementEnabled).as("Elements should be enabled").isTrue();
                }
                break;
            case "disabled":
                for (String elementName : elementNames) {
                    softly = new SoftAssertions();
                    WebElement webElement = loadWebElement(elementName, pageClassName);
                    boolean isElementEnabled = driverManagerUtils.isElementEnabled(webElement);
                    softly.assertThat(isElementEnabled).as("Elements should be disabled").isFalse();
                }
                break;
        }
        assert softly != null;
        softly.assertAll();
    }


    /**
     * Step to check that the element should be checked or not on the page
     */
    @Then("^I verify that the field (.*) should be (checked|unchecked) on the (.*)$")
    public void i_verify_that_the_checkbox_is_checked_unchecked_on_the_page(String elementName, String assertionType, String pageClassName) {
        boolean isElementChecked = driverManagerUtils.isElementSelected(loadWebElement(elementName, pageClassName));

        switch (assertionType) {
            case "checked":
                assertThat(elementName + "element should be checked on the " + pageClassName,  isElementChecked, is(true));
                break;

            case "unchecked":
                assertThat(elementName + "element should be unchecked on the " + pageClassName,  isElementChecked, is(false));
                break;
        }
    }

    @When("I verify the table {string} on the {string}")
    public void verifyTableData(String elementName,String pageClassName, DataTable dataTable){
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<WebElement> webElements = loadWebElements(elementName, pageClassName);
        if(webElements.size()==rows.size()) {
            for(int i=0;i< rows.size();i++){
                Assert.assertEquals(webElements.get(i).getText(),rows.get(i).get("data"));
            }
        }
        else{
            Assert.fail("Both expected and actual data are different...");
        }
    }

    @Then("^I verify that field \"([^\"]*)\" should be (visible|invisible) on the \"([^\"]*)\"$")
    public void verifyWebElementVisibility(String elementName, String assertionType, String pageClassName) {
        String[] data = elementName.split(",");
        for (String ele : data) {
            boolean isElementDisplayed = driverManagerUtils.isElementDisplayed(loadWebElement(ele, pageClassName));
            switch (assertionType) {
                case "visible":
                    assertThat(ele + " field should be displayed on the " + pageClassName, isElementDisplayed, is(true));
                    break;
                case "invisible":
                    assertThat(ele + " field should not be displayed on the " + pageClassName, isElementDisplayed, is(false));
                    break;
                default:
                    throw new InvalidCommonStepSelectionException(assertionType);
            }
        }
    }

    @Then("^I verify the field (.*) having exact or greater then value (.*) on the (.*)$")
    public void verifyWebElementCount(String elementName, String expectedResult, String pageClassName) {
        List<WebElement> elements = loadWebElements(elementName, pageClassName);
        int expected = Integer.valueOf(expectedResult);
        Assert.assertTrue(elements.size() >= expected);
    }

    @Then("^I verify the field (.*) having exact value (.*) on the (.*)$")
    public void verifyTheWebElementCount(String elementName, String expectedResult, String pageClassName) {
        List<WebElement> elements = loadWebElements(elementName, pageClassName);
        int expected = Integer.valueOf(expectedResult);
        Assert.assertEquals(elements.size(), expected, "Data Mismatch");
    }

}