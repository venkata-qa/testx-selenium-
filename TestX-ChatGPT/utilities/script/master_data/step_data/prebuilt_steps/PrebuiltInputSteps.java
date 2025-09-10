package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.common.utils.DateUtils;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class PrebuiltInputSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps.PrebuiltBrowserSteps.class);
    TestContext testContext;

    public PrebuiltInputSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    @When("I enter the value {string} into the {string} on the {string}")
    public void iEnterTheValueInToTheFieldOnThePage(String valueToEnter, String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        driverManagerUtils.enterText(driver, element, valueToEnter);
    }


    @When("I print all the values of {string} on the {string} page")
    public void iPrintTheValueOfElements(String elementName, String pageClassName) {
        List<WebElement> webElements = loadWebElements(elementName, pageClassName);
        for(WebElement we:webElements)
        {
            System.out.println("Element Value is "+we.getText());
        }
    }

    @When("I enter the data for the following fields on the {string}")
    public void iEnterValuesInToTheFieldsOnThePage(String pageClassName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : rows) {
            WebElement webElement = loadWebElement(column.get("fieldName"), pageClassName);
            driverManagerUtils.enterText(driver, webElement, column.get("data"));
        }
    }


    @When("I enter the values {string} into the {string} on the {string}")
    public void iEnterValues_InToTheFieldsOnThePage(String pageClassName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : rows) {
            WebElement webElement = loadWebElement(column.get("fieldName"), pageClassName);
            driverManagerUtils.enterText(driver, webElement, column.get("data"));
        }
    }

    @When("I click the {string} on the {string}")
    public void iClickTheElementOnThePage(String elementName, String pageClassName) {
        driverManagerUtils.clickElement(driver, loadWebElement(elementName, pageClassName));
    }

    @When("I click the following elements on the {string}")
    public void iClickTheElementsOnThePage(String pageClassName, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> column : rows) {
            WebElement webElement = loadWebElement(column.get("fieldName"), pageClassName);
            driverManagerUtils.clickElement(driver, webElement);
        }
    }

    @When("I click the {string} using java script executor on the {string}")
    public void ClickJSElement(String elementName, String pageClassName) throws Exception {
        driverManagerUtils.clickJSElement(loadWebElement(elementName, pageClassName));
    }

    @When("I double click the {string} on the {string}")
    public void iDoubleClickTheElementOnThePage(String elementName, String pageClassName) {
        driverManagerUtils.doubleClickTheElement(loadWebElement(elementName, pageClassName));
    }


    @When("I select the {string} option type with value {string} from the {string} dropdown menu on the {string}")
    public void iSelectFromDropDown(String optionType, String optionValue, String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        driverManagerUtils.selectOptionFromDropdown(element, optionType, optionValue);
    }

    @When("I accept the alert")
    public void acceptAlert() {
        driverManagerUtils.acceptTheAlert();
    }

    @When("I dismiss the alert")
    public void dismissAlert() {
        driverManagerUtils.dismissTheAlert();
    }

    @When("I enter the date as today with {int} days into the {string} on the {string}")
    public void enterDate(int days, String elementName, String pageClassName) {
        String dateFormatPattern = "yyyy-MM-dd";
        WebElement element = loadWebElement(elementName, pageClassName);
        String dateValue = DateUtils.getDate(days, dateFormatPattern);
        driverManagerUtils.enterText(driver, element, dateValue);
        LOGGER.info("Enter the date: {}", dateValue);
    }
}
