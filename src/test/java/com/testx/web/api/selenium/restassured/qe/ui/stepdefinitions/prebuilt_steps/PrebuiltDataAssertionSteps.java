
package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.common.utils.FileUtil;
import com.testx.web.api.selenium.restassured.qe.ui.config.Configuration;
import com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.custom_exceptions.InvalidCommonStepSelectionException;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
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

    /**
     * Asserts that the value at the specified fieldName in the actualJson object
     * equals the value at the same fieldName in the expectedJson object.
     *
     * @param actualJsonFormat   The actual JSON data.
     * @param expectedJsonFormat The expected JSON data.
     * @param list    The data table which contains key name.
     */
    @When("I verify {string} json with {string} json")
    public void verifyJsonFormatData(String actualJsonFormat, String expectedJsonFormat, List<Map<String, String>> list) throws Exception {
        JSONObject actualJson = new JSONObject(actualJsonFormat);
        JSONObject expectedJson = new JSONObject(expectedJsonFormat);
        for (Map<String, String> map : list) {
            String[] names = map.get("fieldName").toString().split("\\.");
            if (names.length == 1) {
                Assert.assertEquals(actualJson.get(map.get("fieldName")), expectedJson.get(map.get("fieldName")));
            }
            else if (names.length == 2) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getString(names[1]), expectedJson.getJSONObject(names[0]).getString(names[1]));
            }
            else if (names.length == 3) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getJSONObject(names[1]).getString(names[2]), expectedJson.getJSONObject(names[0]).getJSONObject(names[1]).getString(names[2]));
            }
            else if (names.length == 4) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getJSONObject(names[1]).getJSONObject(names[2]).getString(names[3]), expectedJson.getJSONObject(names[0]).getJSONObject(names[1]).getJSONObject(names[2]).getString(names[3]));
            }
            else{
                LOGGER.info("Incorrect data found");
            }
        }
    }

    @When("I verify file {string} with {string} field on the {string}")
    public void verifyThePageTitleUsingDT(String fileName, String elementName, String pageClassName, List<Map<String,String>> list) throws Exception {
        String actualResult= loadWebElement(elementName, pageClassName).getText();
        String expectedResult= FileUtil.readFileAsString(System.getProperty("user.dir")+"/src/test/resources/external_downloads/"+fileName+".json");
        JSONObject actualJson=new JSONObject(actualResult);
        JSONObject expectedJson=new JSONObject(expectedResult);
        for(Map<String,String> map:list) {
            String[] names=map.get("fieldName").toString().split("\\.");
            if(names.length==1) {
                Assert.assertEquals(actualJson.get(map.get("fieldName")), expectedJson.get(map.get("fieldName")));
            }
            else if(names.length==2) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getString(names[1]), expectedJson.getJSONObject(names[0]).getString(names[1]));
            }
            else if(names.length==3) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getJSONObject(names[1]).getString(names[2]), expectedJson.getJSONObject(names[0]).getJSONObject(names[1]).getString(names[2]));
            }
            else if(names.length==4) {
                Assert.assertEquals(actualJson.getJSONObject(names[0]).getJSONObject(names[1]).getJSONObject(names[2]).getString(names[3]), expectedJson.getJSONObject(names[0]).getJSONObject(names[1]).getJSONObject(names[2]).getString(names[3]));
            }
            else{
                LOGGER.info("Incorrect data found");
            }
        }
    }

    @When("^I verify value \"([^\"]*)\" present for \"([^\"]*)\" on the \"([^\"]*)\"$")
    public void iVerifyValuePresentForFieldOnThePage(String expectedText, String elementName, String pageClassName) {
        String[] expectedData=expectedText.split(",");
        String[] eleList=elementName.split(",");
        for(int i=0;i<expectedData.length;i++) {
            WebElement element = loadWebElement(eleList[i], pageClassName);
            String actualText = driverManagerUtils.getElementText(driver, element);
            assertThat("Element text not matching", actualText, is(equalTo(expectedData[i])));
        }
    }

    @When("^I verify the value contains \"([^\"]*)\" present for \"([^\"]*)\" on the \"([^\"]*)\"$")
    public void iVerifyTheValueContainsPresentForFieldOnThePage(String expectedText, String elementName, String pageClassName) {
        String[] expectedData=expectedText.split(",");
        String[] eleList=elementName.split(",");
        for(int i=0;i<expectedData.length;i++) {
            WebElement element = loadWebElement(eleList[i], pageClassName);
            String actualText = driverManagerUtils.getElementText(driver, element);
            assertThat("Element text not matching for index "+i, actualText, is(containsString(expectedData[i])));
        }
    }


    @Then("^I verify that following text should (exactly|partially) matches the text of the (.*) field on the (.*)$")
    public void verifyWebElementTextUsingDT(String assertionType, String elementName, String pageClassName, DataTable dataTable) {
        List<List<String>> data = dataTable.asLists();
        List<WebElement> elements= loadWebElements(elementName, pageClassName);
        for(int i=0;i<elements.size();i++) {
            String expectedText = data.get(0).get(i);
            String actualText = driverManagerUtils.getElementText(driver, elements.get(i));

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
    }
}