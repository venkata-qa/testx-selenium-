
package com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps;

import com.testx.web.api.selenium.restassured.qe.common.utils.DateUtils;
import com.testx.web.api.selenium.restassured.qe.ui.context.TestContext;
import com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.BaseSetup;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PrebuiltInputSteps extends BaseSetup {

    private static final Logger LOGGER = LoggerFactory.getLogger(com.testx.web.api.selenium.restassured.qe.ui.stepdefinitions.prebuilt_steps.PrebuiltBrowserSteps.class);
    TestContext testContext;
    public static List<String> data;

    public PrebuiltInputSteps(TestContext context) {
        super(context);
        this.testContext = context;
    }

    @When("I enter {string} into the {string} on the {string}")
    public void iEnterTheValueInToTheFieldOnThePage(String valueToEnter, String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        driverManagerUtils.enterText(driver, element, valueToEnter);
    }

    @When("I enter the value {string} into the {string} on the {string}")
    public void iEnterTheValueInToTheFieldOnPage(String valueToEnter, String elementName, String pageClassName) {
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
    public void iEnterValuesInToTheFieldsOnThePage(String pageClassName, List<Map<String, String>> rows) {
        for (Map<String, String> column : rows) {
            WebElement webElement = loadWebElement(column.get("fieldName"), pageClassName);
            driverManagerUtils.enterText(driver, webElement, column.get("data"));
        }
    }

    @When("I clear the text {string} on the {string}")
    public void iClearTheTextOnThePage(String elementName, String pageClassName) {
        loadWebElement(elementName, pageClassName).clear();
    }

    @When("I press enter button for the {string} on the {string}")
    public void iPressTheEnterOnThePage(String elementName, String pageClassName) {
        loadWebElement(elementName, pageClassName).sendKeys(Keys.ENTER);
    }

    @When("I collect {string} on the {string}")
    public void collectData(String elementName, String pageClassName) {
        data=new ArrayList<>();
        List<WebElement> elements= loadWebElements(elementName, pageClassName);
        for(WebElement ele:elements){
            data.add(ele.getText());
        }
        System.out.println(">>>>>List of data>>>>>>"+data);
    }

    @When("I enter the values on the {string}")
    public void iEnterValues_InToTheFieldsOnThePage(String pageClassName,List<Map<String, String>> rows) {
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

    @When("^I enter value \"([^\"]*)\" into the \"([^\"]*)\" on the \"([^\"]*)\"$")
    public void iEnterValueInToTheFieldOnThePage(String valueToEnter, String elementName, String pageClassName) {
        WebElement element = loadWebElement(elementName, pageClassName);
        driverManagerUtils.enterText(driver, element, valueToEnter);
    }
}
